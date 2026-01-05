import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Polyline, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import Header from '../components/Header';
import Button from '../components/Button';
import LocationSearch from '../components/LocationSearch';
import { fareAPI, rideAPI } from '../services/api';

// Fix for Leaflet marker icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

// Custom icons
const pickupIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const destIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

// Component to recenter map
function MapRecenter({ pickup, destination }) {
    const map = useMap();

    useEffect(() => {
        if (pickup && destination) {
            const bounds = L.latLngBounds([
                [pickup.lat, pickup.lng],
                [destination.lat, destination.lng]
            ]);
            map.fitBounds(bounds, { padding: [50, 50] });
        } else if (pickup) {
            map.setView([pickup.lat, pickup.lng], 14);
        } else if (destination) {
            map.setView([destination.lat, destination.lng], 14);
        }
    }, [pickup, destination, map]);

    return null;
}

export default function CustomerDashboard() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [pickup, setPickup] = useState(null);
    const [destination, setDestination] = useState(null);
    const [routeInfo, setRouteInfo] = useState(null);
    const [fareEstimates, setFareEstimates] = useState([]);
    const [selectedRide, setSelectedRide] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [bookingStatus, setBookingStatus] = useState(null);

    // Default center (Bangalore)
    const defaultCenter = [12.9716, 77.5946];

    useEffect(() => {
        const userData = localStorage.getItem('user');
        if (!userData) {
            navigate('/customer/login');
            return;
        }
        setUser(JSON.parse(userData));
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/');
    };

    const calculateRoute = useCallback(async () => {
        if (!pickup || !destination) return;

        setLoading(true);
        setError('');

        try {
            const response = await fareAPI.calculateFare({
                pickupLat: pickup.lat,
                pickupLng: pickup.lng,
                pickupAddress: pickup.address,
                destinationLat: destination.lat,
                destinationLng: destination.lng,
                destinationAddress: destination.address,
            });

            const data = response.data;

            if (data.fareEstimates && data.fareEstimates.length > 0) {
                setRouteInfo({
                    distanceKm: data.distanceKm,
                    distanceText: data.distanceText,
                    durationMin: data.durationMin,
                    durationText: data.durationText,
                });
                setFareEstimates(data.fareEstimates);
                setSelectedRide(data.fareEstimates[0]);
            } else {
                setError(data.message || 'Could not calculate fare');
            }
        } catch (err) {
            setError('Could not calculate route. Make sure backend is running.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [pickup, destination]);

    useEffect(() => {
        if (pickup && destination) {
            calculateRoute();
        }
    }, [pickup, destination, calculateRoute]);

    const handleBookRide = async () => {
        if (!selectedRide || !pickup || !destination) return;

        // Check for token
        const token = localStorage.getItem('token');
        if (!token) {
            setError('You are not logged in. Please log in again.');
            return;
        }
        console.log('Token exists:', token.substring(0, 20) + '...');

        setLoading(true);
        setError('');

        try {
            const response = await rideAPI.createBooking({
                pickupAddress: pickup.address,
                pickupLat: pickup.lat,
                pickupLng: pickup.lng,
                destinationAddress: destination.address,
                destinationLat: destination.lat,
                destinationLng: destination.lng,
                tripDistanceInKm: selectedRide.distanceKm,
                estimatedTimeMin: selectedRide.durationMin,
                billAmount: selectedRide.totalFare,
                rideType: selectedRide.rideType,
            });

            if (response.data.bookingId) {
                setBookingStatus(response.data);
            } else {
                setError(response.data.message || 'Could not create booking');
            }
        } catch (err) {
            console.error('Booking error:', err);
            if (err.response?.status === 403) {
                setError('Session expired. Please log out and log in again.');
            } else if (err.response?.status === 404) {
                setError('Backend not responding. Make sure Spring Boot is running.');
            } else {
                setError(err.response?.data?.message || 'Error creating booking. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    };

    if (!user) return null;

    return (
        <div className="min-h-screen bg-gray-50">
            <Header />

            <main className="max-w-6xl mx-auto px-6 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Left Column - Map */}
                    <div className="order-2 lg:order-1">
                        <div className="bg-white rounded-2xl shadow-sm overflow-hidden" style={{ height: '500px' }}>
                            <MapContainer
                                center={defaultCenter}
                                zoom={12}
                                style={{ height: '100%', width: '100%' }}
                            >
                                <TileLayer
                                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                />
                                <MapRecenter pickup={pickup} destination={destination} />

                                {pickup && (
                                    <Marker position={[pickup.lat, pickup.lng]} icon={pickupIcon} />
                                )}
                                {destination && (
                                    <Marker position={[destination.lat, destination.lng]} icon={destIcon} />
                                )}
                                {pickup && destination && (
                                    <Polyline
                                        positions={[
                                            [pickup.lat, pickup.lng],
                                            [destination.lat, destination.lng]
                                        ]}
                                        color="#000"
                                        weight={3}
                                        dashArray="10, 10"
                                    />
                                )}
                            </MapContainer>
                        </div>
                    </div>

                    {/* Right Column - Controls */}
                    <div className="order-1 lg:order-2 space-y-6">
                        {/* Welcome */}
                        <div className="bg-white rounded-2xl p-6 shadow-sm">
                            <div className="flex items-center justify-between">
                                <div>
                                    <h1 className="text-2xl font-bold text-gray-900">
                                        Hello, {user.email?.split('@')[0]} 👋
                                    </h1>
                                    <p className="text-gray-500">Where would you like to go?</p>
                                </div>
                                <Button variant="secondary" onClick={handleLogout}>
                                    Logout
                                </Button>
                            </div>
                        </div>

                        {/* Location Input */}
                        <div className="bg-white rounded-2xl p-6 shadow-sm">
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Plan your trip</h2>
                            <div className="space-y-4">
                                <LocationSearch
                                    placeholder="Enter pickup location"
                                    onSelect={setPickup}
                                    icon={<div className="w-3 h-3 bg-green-500 rounded-full"></div>}
                                />
                                <LocationSearch
                                    placeholder="Where to?"
                                    onSelect={setDestination}
                                    icon={<div className="w-3 h-3 bg-red-500 rounded-full"></div>}
                                />
                            </div>

                            {/* Route Info */}
                            {routeInfo && (
                                <div className="mt-4 p-4 bg-gray-50 rounded-xl">
                                    <div className="flex items-center justify-between text-sm">
                                        <div className="flex items-center gap-2">
                                            <span className="text-gray-600">📍 {routeInfo.distanceText}</span>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <span className="text-gray-600">⏱️ {routeInfo.durationText}</span>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {error && (
                                <div className="mt-4 p-4 bg-red-50 text-red-600 rounded-xl text-sm">
                                    {error}
                                </div>
                            )}
                        </div>

                        {/* Ride Options */}
                        {fareEstimates.length > 0 && (
                            <div className="bg-white rounded-2xl p-6 shadow-sm">
                                <h2 className="text-lg font-semibold text-gray-900 mb-4">Choose a ride</h2>
                                <div className="space-y-3">
                                    {fareEstimates.map((ride) => (
                                        <button
                                            key={ride.rideType}
                                            onClick={() => setSelectedRide(ride)}
                                            className={`w-full p-4 border rounded-xl transition-all text-left flex items-center justify-between ${selectedRide?.rideType === ride.rideType
                                                ? 'border-black bg-gray-50'
                                                : 'border-gray-200 hover:border-gray-400'
                                                }`}
                                        >
                                            <div className="flex items-center gap-4">
                                                <div className="text-3xl">{ride.rideIcon}</div>
                                                <div>
                                                    <div className="font-semibold text-gray-900">{ride.rideName}</div>
                                                    <div className="text-sm text-gray-500">
                                                        {ride.durationText} • {ride.distanceText}
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <div className="font-bold text-gray-900 text-lg">₹{ride.totalFare}</div>
                                                <div className="text-xs text-gray-400">
                                                    Base ₹{ride.baseFare} + ₹{ride.distanceFare}
                                                </div>
                                            </div>
                                        </button>
                                    ))}
                                </div>

                                <div className="mt-6">
                                    <Button
                                        fullWidth
                                        onClick={handleBookRide}
                                        disabled={!selectedRide || loading || bookingStatus}
                                        loading={loading}
                                    >
                                        Book {selectedRide?.rideName} • ₹{selectedRide?.totalFare}
                                    </Button>
                                </div>
                            </div>
                        )}

                        {/* Booking Status */}
                        {bookingStatus && (
                            <div className="bg-green-50 border border-green-200 rounded-2xl p-6">
                                <div className="flex items-center gap-3 mb-4">
                                    <div className="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                        </svg>
                                    </div>
                                    <div>
                                        <h3 className="font-semibold text-green-800">Ride Booked!</h3>
                                        <p className="text-sm text-green-600">{bookingStatus.message}</p>
                                    </div>
                                </div>

                                <div className="bg-white rounded-xl p-4 space-y-2">
                                    <div className="flex justify-between">
                                        <span className="text-gray-500">Booking ID</span>
                                        <span className="font-medium">#{bookingStatus.bookingId}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-500">Status</span>
                                        <span className="font-medium text-yellow-600">{bookingStatus.tripStatus}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-500">Fare</span>
                                        <span className="font-bold">₹{bookingStatus.billAmount}</span>
                                    </div>
                                </div>

                                <button
                                    onClick={() => {
                                        setBookingStatus(null);
                                        setPickup(null);
                                        setDestination(null);
                                        setFareEstimates([]);
                                        setRouteInfo(null);
                                    }}
                                    className="mt-4 w-full py-2 text-green-700 font-medium"
                                >
                                    Book Another Ride
                                </button>
                            </div>
                        )}


                        {/* Loading State */}
                        {loading && (
                            <div className="bg-white rounded-2xl p-8 shadow-sm text-center">
                                <div className="animate-spin h-8 w-8 border-4 border-black border-t-transparent rounded-full mx-auto mb-4"></div>
                                <p className="text-gray-500">Calculating fares...</p>
                            </div>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
}
