import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Button from '../components/Button';
import { rideAPI } from '../services/api';

export default function DriverDashboard() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [isOnline, setIsOnline] = useState(false);
    const [pendingBookings, setPendingBookings] = useState([]);
    const [myBookings, setMyBookings] = useState([]);
    const [currentBooking, setCurrentBooking] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const userData = localStorage.getItem('user');
        if (!userData) {
            navigate('/driver/login');
            return;
        }
        setUser(JSON.parse(userData));
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/');
    };

    // Fetch pending bookings when online
    const fetchPendingBookings = useCallback(async () => {
        if (!isOnline) return;

        try {
            const response = await rideAPI.getPendingBookings();
            setPendingBookings(response.data);
        } catch (err) {
            console.error('Error fetching pending bookings:', err);
        }
    }, [isOnline]);

    // Fetch my accepted bookings
    const fetchMyBookings = useCallback(async () => {
        try {
            const response = await rideAPI.getDriverBookings();
            setMyBookings(response.data);
            // Check if there's an active booking
            const active = response.data.find(b =>
                b.tripStatus === 'ACCEPTED' || b.tripStatus === 'IN_PROGRESS'
            );
            setCurrentBooking(active || null);
        } catch (err) {
            console.error('Error fetching my bookings:', err);
        }
    }, []);

    // Poll for new bookings when online
    useEffect(() => {
        if (isOnline) {
            fetchPendingBookings();
            fetchMyBookings();
            const interval = setInterval(() => {
                fetchPendingBookings();
                fetchMyBookings();
            }, 5000); // Poll every 5 seconds
            return () => clearInterval(interval);
        }
    }, [isOnline, fetchPendingBookings, fetchMyBookings]);

    const handleAcceptBooking = async (bookingId) => {
        setLoading(true);
        setError('');
        try {
            const response = await rideAPI.acceptBooking(bookingId);
            if (response.data.bookingId) {
                setCurrentBooking(response.data);
                fetchPendingBookings();
                fetchMyBookings();
            } else {
                setError(response.data.message || 'Could not accept booking');
            }
        } catch (err) {
            setError('Error accepting booking');
        } finally {
            setLoading(false);
        }
    };

    const handleRejectBooking = async (bookingId) => {
        try {
            await rideAPI.rejectBooking(bookingId);
            fetchPendingBookings();
        } catch (err) {
            console.error('Error rejecting booking:', err);
        }
    };

    const handleStartTrip = async () => {
        if (!currentBooking) return;
        setLoading(true);
        try {
            const response = await rideAPI.startTrip(currentBooking.bookingId);
            setCurrentBooking(response.data);
        } catch (err) {
            setError('Error starting trip');
        } finally {
            setLoading(false);
        }
    };

    const handleCompleteTrip = async () => {
        if (!currentBooking) return;
        setLoading(true);
        try {
            const response = await rideAPI.completeTrip(currentBooking.bookingId);
            alert(`Trip completed! Earned: ₹${response.data.billAmount}`);
            setCurrentBooking(null);
            fetchMyBookings();
        } catch (err) {
            setError('Error completing trip');
        } finally {
            setLoading(false);
        }
    };

    if (!user) return null;

    const stats = [
        { label: "Today's Earnings", value: '₹0', icon: '💰' },
        { label: 'Rides Completed', value: myBookings.filter(b => b.tripStatus === 'COMPLETED').length.toString(), icon: '🚗' },
        { label: 'Hours Online', value: '0h', icon: '⏱️' },
        { label: 'Rating', value: '5.0', icon: '⭐' },
    ];

    return (
        <div className="min-h-screen bg-gray-50">
            <Header />

            <main className="max-w-4xl mx-auto px-6 py-8">
                {/* Profile & Status */}
                <div className="bg-white rounded-2xl p-6 shadow-sm mb-6">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                            <div className="w-16 h-16 bg-black rounded-full flex items-center justify-center text-white text-2xl font-bold">
                                {user.name?.[0]?.toUpperCase() || 'D'}
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-gray-900">
                                    Welcome, {user.name?.split(' ')[0] || 'Driver'}!
                                </h1>
                            </div>
                        </div>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setIsOnline(!isOnline)}
                                className={`px-6 py-3 rounded-full font-medium transition-all ${isOnline
                                    ? 'bg-green-500 text-white'
                                    : 'bg-gray-200 text-gray-700'
                                    }`}
                            >
                                {isOnline ? '🟢 Online' : '⚫ Offline'}
                            </button>
                            <Button variant="secondary" onClick={handleLogout}>
                                Logout
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                    {stats.map((stat, index) => (
                        <div key={index} className="bg-white rounded-xl p-4 shadow-sm">
                            <div className="text-2xl mb-2">{stat.icon}</div>
                            <div className="text-2xl font-bold text-gray-900">{stat.value}</div>
                            <div className="text-sm text-gray-500">{stat.label}</div>
                        </div>
                    ))}
                </div>

                {/* Current Active Booking */}
                {currentBooking && (
                    <div className="bg-black text-white rounded-2xl p-6 mb-6">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-lg font-semibold">Active Ride</h2>
                            <span className={`px-3 py-1 rounded-full text-sm ${currentBooking.tripStatus === 'ACCEPTED' ? 'bg-yellow-500' : 'bg-green-500'
                                }`}>
                                {currentBooking.tripStatus === 'ACCEPTED' ? 'Ready to Start' : 'In Progress'}
                            </span>
                        </div>

                        <div className="space-y-3 mb-6">
                            <div className="flex items-start gap-3">
                                <div className="w-3 h-3 bg-green-400 rounded-full mt-1.5"></div>
                                <div>
                                    <div className="text-gray-400 text-xs">PICKUP</div>
                                    <div className="text-white">{currentBooking.pickupAddress}</div>
                                </div>
                            </div>
                            <div className="flex items-start gap-3">
                                <div className="w-3 h-3 bg-red-400 rounded-full mt-1.5"></div>
                                <div>
                                    <div className="text-gray-400 text-xs">DROP</div>
                                    <div className="text-white">{currentBooking.destinationAddress}</div>
                                </div>
                            </div>
                        </div>

                        <div className="flex justify-between items-center mb-4 text-sm">
                            <span>Distance: {currentBooking.tripDistanceInKm} km</span>
                            <span>Time: {currentBooking.estimatedTimeMin} mins</span>
                            <span className="text-xl font-bold">₹{currentBooking.billAmount}</span>
                        </div>

                        {currentBooking.tripStatus === 'ACCEPTED' && (
                            <Button
                                fullWidth
                                className="!bg-green-500 hover:!bg-green-600"
                                onClick={handleStartTrip}
                                loading={loading}
                            >
                                Start Trip
                            </Button>
                        )}
                        {currentBooking.tripStatus === 'IN_PROGRESS' && (
                            <Button
                                fullWidth
                                className="!bg-blue-500 hover:!bg-blue-600"
                                onClick={handleCompleteTrip}
                                loading={loading}
                            >
                                Complete Trip - ₹{currentBooking.billAmount}
                            </Button>
                        )}
                    </div>
                )}

                {/* Go Online CTA */}
                {!isOnline && !currentBooking && (
                    <div className="bg-black text-white rounded-2xl p-8 text-center mb-6">
                        <h2 className="text-2xl font-bold mb-2">Ready to earn?</h2>
                        <p className="text-gray-300 mb-6">Go online to start accepting ride requests</p>
                        <Button
                            className="!bg-white !text-black hover:!bg-gray-100"
                            onClick={() => setIsOnline(true)}
                        >
                            Go Online
                        </Button>
                    </div>
                )}

                {/* Error */}
                {error && (
                    <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6">
                        {error}
                    </div>
                )}

                {/* Incoming Ride Requests */}
                {isOnline && !currentBooking && (
                    <div className="bg-white rounded-2xl p-6 shadow-sm mb-6">
                        <h2 className="text-lg font-semibold text-gray-900 mb-4">
                            Incoming Requests ({pendingBookings.length})
                        </h2>

                        {pendingBookings.length === 0 ? (
                            <div className="text-center py-8 text-gray-400">
                                <div className="animate-pulse">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 mx-auto mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    <p>Waiting for ride requests...</p>
                                </div>
                            </div>
                        ) : (
                            <div className="space-y-4">
                                {pendingBookings.map((booking) => (
                                    <div key={booking.bookingId} className="border border-gray-200 rounded-xl p-4">
                                        <div className="flex justify-between items-start mb-3">
                                            <div className="flex-1">
                                                <div className="flex items-center gap-2 mb-2">
                                                    <span className="text-2xl">
                                                        {booking.rideType === 'AUTO' && '🛺'}
                                                        {booking.rideType === 'BIKE' && '🏍️'}
                                                        {booking.rideType === 'CAR' && '🚗'}
                                                        {booking.rideType === 'PREMIUM' && '🚙'}
                                                    </span>
                                                    <span className="font-medium">{booking.rideType}</span>
                                                </div>
                                                <div className="space-y-1 text-sm">
                                                    <div className="flex items-center gap-2">
                                                        <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                                                        <span className="text-gray-600 truncate">{booking.pickupAddress}</span>
                                                    </div>
                                                    <div className="flex items-center gap-2">
                                                        <div className="w-2 h-2 bg-red-500 rounded-full"></div>
                                                        <span className="text-gray-600 truncate">{booking.destinationAddress}</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="text-right">
                                                <div className="text-xl font-bold text-gray-900">₹{booking.billAmount}</div>
                                                <div className="text-xs text-gray-400">
                                                    {booking.tripDistanceInKm} km • {booking.estimatedTimeMin} min
                                                </div>
                                            </div>
                                        </div>
                                        <div className="flex gap-3">
                                            <Button
                                                fullWidth
                                                onClick={() => handleAcceptBooking(booking.bookingId)}
                                                loading={loading}
                                            >
                                                Accept
                                            </Button>
                                            <Button
                                                variant="secondary"
                                                fullWidth
                                                onClick={() => handleRejectBooking(booking.bookingId)}
                                            >
                                                Decline
                                            </Button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* Recent Activity */}
                <div className="bg-white rounded-2xl p-6 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h2>
                    {myBookings.length === 0 ? (
                        <div className="text-center py-8 text-gray-400">
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 mx-auto mb-3 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <p>No activity yet. Go online to start earning!</p>
                        </div>
                    ) : (
                        <div className="space-y-3">
                            {myBookings.slice(0, 5).map((booking) => (
                                <div key={booking.bookingId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                    <div>
                                        <div className="font-medium text-gray-900">
                                            {booking.pickupAddress?.split(',')[0]} → {booking.destinationAddress?.split(',')[0]}
                                        </div>
                                        <div className="text-xs text-gray-400">
                                            {new Date(booking.bookedAt).toLocaleString()}
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <div className="font-bold text-gray-900">₹{booking.billAmount}</div>
                                        <div className={`text-xs ${booking.tripStatus === 'COMPLETED' ? 'text-green-500' : 'text-gray-400'
                                            }`}>
                                            {booking.tripStatus}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}
