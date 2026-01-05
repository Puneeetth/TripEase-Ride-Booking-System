import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add token to requests if available
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Auth APIs
export const authAPI = {
    registerDriver: (data) => api.post('/auth/register/driver', data),
    registerCustomer: (data) => api.post('/auth/register/customer', data),
    loginDriver: (data) => api.post('/auth/login/driver', data),
    loginCustomer: (data) => api.post('/auth/login/customer', data),
};

// Customer APIs
export const customerAPI = {
    getCustomer: (id) => api.get(`/customer/get/customer-id/${id}`),
};

// Driver APIs
export const driverAPI = {
    getDriver: (id) => api.get(`/driver/get/driver-id/${id}`),
};

// Driver Documents APIs
export const driverDocumentsAPI = {
    submitDocuments: (data) => api.post('/driver/documents/submit', data),
    getDocumentStatus: (driverId) => api.get(`/driver/documents/status/${driverId}`),
};

// Booking APIs
export const bookingAPI = {
    createBooking: (data) => api.post('/booking/book', data),
};

// Fare APIs
export const fareAPI = {
    calculateFare: (data) => api.post('/fare/calculate', data),
};

// Ride APIs (new booking system)
export const rideAPI = {
    createBooking: (data) => api.post('/ride/book', data),
    getPendingBookings: () => api.get('/ride/pending'),
    acceptBooking: (bookingId) => api.post(`/ride/accept/${bookingId}`),
    rejectBooking: (bookingId) => api.post(`/ride/reject/${bookingId}`),
    getBooking: (bookingId) => api.get(`/ride/${bookingId}`),
    getCustomerBookings: () => api.get('/ride/customer/bookings'),
    getDriverBookings: () => api.get('/ride/driver/bookings'),
    startTrip: (bookingId) => api.post(`/ride/start/${bookingId}`),
    completeTrip: (bookingId) => api.post(`/ride/complete/${bookingId}`),
};

export default api;
