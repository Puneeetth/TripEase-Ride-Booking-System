package com.example.tripease.service;

import com.example.tripease.dto.request.BookingRequest;
import com.example.tripease.dto.response.BookingResponse;
import com.example.tripease.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Legacy BookingService - Deprecated
 * Use RideBookingService instead for new booking functionality
 */
@Service
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;

    // Legacy method - not used anymore
    // New booking flow uses RideBookingService
    public BookingResponse bookCab(BookingRequest bookingRequest, int customerId) {
        throw new UnsupportedOperationException("Use RideBookingService.createBooking instead");
    }
}
