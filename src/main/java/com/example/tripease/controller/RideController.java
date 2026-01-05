package com.example.tripease.controller;

import com.example.tripease.dto.request.CreateBookingRequest;
import com.example.tripease.dto.response.BookingDetailsResponse;
import com.example.tripease.service.RideBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ride")
@RequiredArgsConstructor
public class RideController {

    private final RideBookingService rideBookingService;

    /**
     * Customer creates a new booking
     */
    @PostMapping("/book")
    public ResponseEntity<BookingDetailsResponse> createBooking(@RequestBody CreateBookingRequest request) {
        try {
            System.out.println("=== Booking Request Received ===");
            System.out.println("Pickup: " + request.getPickupAddress());
            System.out.println("Destination: " + request.getDestinationAddress());
            System.out.println("RideType: " + request.getRideType());

            BookingDetailsResponse response = rideBookingService.createBooking(request);
            System.out.println("Booking created with ID: " + response.getBookingId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    BookingDetailsResponse.builder()
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get all pending bookings (for drivers)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<BookingDetailsResponse>> getPendingBookings() {
        List<BookingDetailsResponse> bookings = rideBookingService.getPendingBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Driver accepts a booking
     */
    @PostMapping("/accept/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> acceptBooking(@PathVariable int bookingId) {
        try {
            BookingDetailsResponse response = rideBookingService.acceptBooking(bookingId);
            if (response.getBookingId() == 0) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    BookingDetailsResponse.builder()
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Driver rejects a booking
     */
    @PostMapping("/reject/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> rejectBooking(@PathVariable int bookingId) {
        BookingDetailsResponse response = rideBookingService.rejectBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get booking details
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBooking(@PathVariable int bookingId) {
        BookingDetailsResponse response = rideBookingService.getBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer's bookings
     */
    @GetMapping("/customer/bookings")
    public ResponseEntity<List<BookingDetailsResponse>> getCustomerBookings() {
        List<BookingDetailsResponse> bookings = rideBookingService.getCustomerBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get driver's bookings
     */
    @GetMapping("/driver/bookings")
    public ResponseEntity<List<BookingDetailsResponse>> getDriverBookings() {
        List<BookingDetailsResponse> bookings = rideBookingService.getDriverBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Driver starts the trip
     */
    @PostMapping("/start/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> startTrip(@PathVariable int bookingId) {
        BookingDetailsResponse response = rideBookingService.startTrip(bookingId);
        if (response.getBookingId() == 0) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Driver completes the trip
     */
    @PostMapping("/complete/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> completeTrip(@PathVariable int bookingId) {
        BookingDetailsResponse response = rideBookingService.completeTrip(bookingId);
        if (response.getBookingId() == 0) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
