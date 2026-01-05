package com.example.tripease.service;

import com.example.tripease.Enum.TripStatus;
import com.example.tripease.dto.request.CreateBookingRequest;
import com.example.tripease.dto.response.BookingDetailsResponse;
import com.example.tripease.model.Booking;
import com.example.tripease.model.User;
import com.example.tripease.repository.BookingRepository;
import com.example.tripease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideBookingService {

        private final BookingRepository bookingRepository;
        private final UserRepository userRepository;

        /**
         * Create a new booking (called by customer)
         */
        public BookingDetailsResponse createBooking(CreateBookingRequest request) {
                // Get current user from security context
                var auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("Auth: " + auth);
                System.out.println("Auth Name: " + (auth != null ? auth.getName() : "null"));
                System.out.println("Auth Principal: " + (auth != null ? auth.getPrincipal() : "null"));

                String email = auth != null ? auth.getName() : null;
                if (email == null || email.equals("anonymousUser")) {
                        System.err.println("User not authenticated!");
                        return BookingDetailsResponse.builder()
                                        .message("User not authenticated. Please log in again.")
                                        .build();
                }

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found: " + email));

                System.out.println("User found: " + user.getEmail() + ", ReferenceId: " + user.getReferenceId());

                Booking booking = Booking.builder()
                                .customerId(user.getReferenceId())
                                .customerEmail(email)
                                .pickupAddress(request.getPickupAddress())
                                .pickupLat(request.getPickupLat())
                                .pickupLng(request.getPickupLng())
                                .destinationAddress(request.getDestinationAddress())
                                .destinationLat(request.getDestinationLat())
                                .destinationLng(request.getDestinationLng())
                                .tripDistanceInKm(request.getTripDistanceInKm())
                                .estimatedTimeMin(request.getEstimatedTimeMin())
                                .billAmount(request.getBillAmount())
                                .rideType(request.getRideType())
                                .tripStatus(TripStatus.PENDING)
                                .build();

                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponse(savedBooking, "Booking created successfully. Waiting for driver.");
        }

        /**
         * Get all pending bookings (for drivers to see)
         */
        public List<BookingDetailsResponse> getPendingBookings() {
                List<Booking> pendingBookings = bookingRepository
                                .findByTripStatusAndDriverIdIsNullOrderByBookedAtDesc(TripStatus.PENDING);

                return pendingBookings.stream()
                                .map(b -> mapToResponse(b, null))
                                .collect(Collectors.toList());
        }

        /**
         * Driver accepts a booking
         */
        public BookingDetailsResponse acceptBooking(int bookingId) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (booking.getTripStatus() != TripStatus.PENDING) {
                        return BookingDetailsResponse.builder()
                                        .message("Booking is no longer available")
                                        .build();
                }

                if (booking.getDriverId() != null) {
                        return BookingDetailsResponse.builder()
                                        .message("Booking already accepted by another driver")
                                        .build();
                }

                booking.setDriverId(user.getReferenceId());
                booking.setDriverEmail(email);
                booking.setTripStatus(TripStatus.ACCEPTED);

                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponse(savedBooking, "Booking accepted successfully!");
        }

        /**
         * Driver rejects a booking (just marks as rejected, customer can rebook)
         */
        public BookingDetailsResponse rejectBooking(int bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (booking.getTripStatus() != TripStatus.PENDING) {
                        return BookingDetailsResponse.builder()
                                        .message("Booking is no longer pending")
                                        .build();
                }

                // For now, just reject. In a real app, you might reassign to another driver
                booking.setTripStatus(TripStatus.REJECTED);
                Booking savedBooking = bookingRepository.save(booking);

                return mapToResponse(savedBooking, "Booking rejected");
        }

        /**
         * Get booking by ID
         */
        public BookingDetailsResponse getBooking(int bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));
                return mapToResponse(booking, null);
        }

        /**
         * Get customer's bookings
         */
        public List<BookingDetailsResponse> getCustomerBookings() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<Booking> bookings = bookingRepository.findByCustomerIdOrderByBookedAtDesc(user.getReferenceId());
                return bookings.stream()
                                .map(b -> mapToResponse(b, null))
                                .collect(Collectors.toList());
        }

        /**
         * Get driver's accepted bookings
         */
        public List<BookingDetailsResponse> getDriverBookings() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<Booking> bookings = bookingRepository.findByDriverIdOrderByBookedAtDesc(user.getReferenceId());
                return bookings.stream()
                                .map(b -> mapToResponse(b, null))
                                .collect(Collectors.toList());
        }

        /**
         * Start trip (driver marks as in progress)
         */
        public BookingDetailsResponse startTrip(int bookingId) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (!booking.getDriverId().equals(user.getReferenceId())) {
                        return BookingDetailsResponse.builder()
                                        .message("You are not assigned to this booking")
                                        .build();
                }

                booking.setTripStatus(TripStatus.IN_PROGRESS);
                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponse(savedBooking, "Trip started!");
        }

        /**
         * Complete trip
         */
        public BookingDetailsResponse completeTrip(int bookingId) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (!booking.getDriverId().equals(user.getReferenceId())) {
                        return BookingDetailsResponse.builder()
                                        .message("You are not assigned to this booking")
                                        .build();
                }

                booking.setTripStatus(TripStatus.COMPLETED);
                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponse(savedBooking, "Trip completed! Fare: ₹" + booking.getBillAmount());
        }

        private BookingDetailsResponse mapToResponse(Booking booking, String message) {
                return BookingDetailsResponse.builder()
                                .bookingId(booking.getBookingId())
                                .customerId(booking.getCustomerId())
                                .customerEmail(booking.getCustomerEmail())
                                .driverId(booking.getDriverId())
                                .driverEmail(booking.getDriverEmail())
                                .pickupAddress(booking.getPickupAddress())
                                .pickupLat(booking.getPickupLat())
                                .pickupLng(booking.getPickupLng())
                                .destinationAddress(booking.getDestinationAddress())
                                .destinationLat(booking.getDestinationLat())
                                .destinationLng(booking.getDestinationLng())
                                .tripDistanceInKm(booking.getTripDistanceInKm())
                                .estimatedTimeMin(booking.getEstimatedTimeMin())
                                .billAmount(booking.getBillAmount())
                                .rideType(booking.getRideType())
                                .tripStatus(booking.getTripStatus())
                                .bookedAt(booking.getBookedAt())
                                .message(message)
                                .build();
        }
}
