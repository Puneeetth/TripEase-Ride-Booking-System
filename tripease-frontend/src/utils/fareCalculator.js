// Fare rates per kilometer for different ride types (in INR)
export const RIDE_RATES = {
    auto: {
        baseRate: 25,      // Base fare
        perKm: 12,         // Per km rate
        perMin: 1,         // Per minute rate
        minFare: 30,       // Minimum fare
    },
    bike: {
        baseRate: 15,
        perKm: 8,
        perMin: 0.5,
        minFare: 20,
    },
    car: {
        baseRate: 50,
        perKm: 15,
        perMin: 2,
        minFare: 80,
    },
    premium: {
        baseRate: 100,
        perKm: 25,
        perMin: 3,
        minFare: 150,
    },
};

/**
 * Calculate fare based on distance and duration
 * @param {string} rideType - Type of ride (auto, bike, car, premium)
 * @param {number} distanceKm - Distance in kilometers
 * @param {number} durationMin - Duration in minutes
 * @returns {object} - Fare details including total fare and breakdown
 */
export function calculateFare(rideType, distanceKm, durationMin) {
    const rates = RIDE_RATES[rideType];

    if (!rates) {
        return { error: 'Invalid ride type' };
    }

    const distanceFare = distanceKm * rates.perKm;
    const timeFare = durationMin * rates.perMin;
    const totalFare = rates.baseRate + distanceFare + timeFare;
    const finalFare = Math.max(totalFare, rates.minFare);

    return {
        rideType,
        baseFare: rates.baseRate,
        distanceFare: Math.round(distanceFare),
        timeFare: Math.round(timeFare),
        totalFare: Math.round(finalFare),
        distance: distanceKm.toFixed(1),
        duration: Math.round(durationMin),
    };
}

/**
 * Calculate distance and duration between two points using Google Distance Matrix
 * @param {object} origin - Origin coordinates { lat, lng }
 * @param {object} destination - Destination coordinates { lat, lng }
 * @returns {Promise<object>} - Distance and duration
 */
export function getDistanceAndDuration(origin, destination) {
    return new Promise((resolve, reject) => {
        if (!window.google) {
            reject(new Error('Google Maps not loaded'));
            return;
        }

        const service = new window.google.maps.DistanceMatrixService();

        service.getDistanceMatrix(
            {
                origins: [new window.google.maps.LatLng(origin.lat, origin.lng)],
                destinations: [new window.google.maps.LatLng(destination.lat, destination.lng)],
                travelMode: window.google.maps.TravelMode.DRIVING,
                unitSystem: window.google.maps.UnitSystem.METRIC,
            },
            (response, status) => {
                if (status === 'OK' && response.rows[0].elements[0].status === 'OK') {
                    const result = response.rows[0].elements[0];
                    resolve({
                        distanceKm: result.distance.value / 1000, // Convert meters to km
                        distanceText: result.distance.text,
                        durationMin: result.duration.value / 60,  // Convert seconds to minutes
                        durationText: result.duration.text,
                    });
                } else {
                    reject(new Error('Could not calculate distance'));
                }
            }
        );
    });
}

/**
 * Get fare estimates for all ride types
 * @param {number} distanceKm - Distance in kilometers
 * @param {number} durationMin - Duration in minutes
 * @returns {array} - Array of fare estimates for all ride types
 */
export function getAllFareEstimates(distanceKm, durationMin) {
    return Object.keys(RIDE_RATES).map(rideType => ({
        ...calculateFare(rideType, distanceKm, durationMin),
        icon: getRideIcon(rideType),
        name: getRideName(rideType),
    }));
}

function getRideIcon(rideType) {
    const icons = {
        auto: '🛺',
        bike: '🏍️',
        car: '🚗',
        premium: '🚙',
    };
    return icons[rideType] || '🚗';
}

function getRideName(rideType) {
    const names = {
        auto: 'Auto',
        bike: 'Bike',
        car: 'Car',
        premium: 'Premium',
    };
    return names[rideType] || rideType;
}
