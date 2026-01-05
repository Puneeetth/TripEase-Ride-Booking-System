package com.example.tripease.service;

import com.example.tripease.Enum.TripStatus;
import com.example.tripease.dto.request.ExternalBookingRequest;
import com.example.tripease.dto.response.ExternalBookingResponse;
import com.example.tripease.model.Booking;
import com.example.tripease.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for handling external booking requests from other systems (e.g.,
 * Integrated Travel Management)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalRideService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;

    @Value("${integration.api.key}")
    private String apiKey;

    /**
     * Create a booking from an external system
     */
    public ExternalBookingResponse createExternalBooking(ExternalBookingRequest request) {
        try {
            log.info("Creating external booking from {} with external ID: {}",
                    request.getSourceSystem(), request.getExternalBookingId());

            // Create booking entity
            Booking booking = Booking.builder()
                    .customerId(0) // No TripEase customer for external bookings
                    .customerEmail(request.getPassengerEmail())
                    .pickupAddress(request.getPickupAddress())
                    .pickupLat(request.getPickupLat() != null ? request.getPickupLat() : 0.0)
                    .pickupLng(request.getPickupLng() != null ? request.getPickupLng() : 0.0)
                    .destinationAddress(request.getDestinationAddress())
                    .destinationLat(request.getDestinationLat() != null ? request.getDestinationLat() : 0.0)
                    .destinationLng(request.getDestinationLng() != null ? request.getDestinationLng() : 0.0)
                    .tripDistanceInKm(request.getTripDistanceInKm() != null ? request.getTripDistanceInKm() : 0.0)
                    .estimatedTimeMin(request.getEstimatedTimeMin() != null ? request.getEstimatedTimeMin() : 30)
                    .billAmount(request.getEstimatedFare() != null ? request.getEstimatedFare() : 0.0)
                    .rideType(request.getRideType())
                    .tripStatus(TripStatus.PENDING)
                    .sourceSystem(request.getSourceSystem())
                    .externalBookingId(request.getExternalBookingId())
                    .isExternalBooking(true)
                    .passengerName(request.getPassengerName())
                    .passengerPhone(request.getPassengerPhone())
                    .specialRequests(request.getSpecialRequests())
                    .build();

            Booking savedBooking = bookingRepository.save(booking);
            log.info("External booking created with TripEase ID: {}", savedBooking.getBookingId());

            return ExternalBookingResponse.builder()
                    .tripEaseBookingId(savedBooking.getBookingId())
                    .sourceSystem(request.getSourceSystem())
                    .externalBookingId(request.getExternalBookingId())
                    .status(TripStatus.PENDING)
                    .estimatedFare(savedBooking.getBillAmount())
                    .estimatedTimeMin(savedBooking.getEstimatedTimeMin())
                    .message("Booking created successfully. Waiting for driver assignment.")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error creating external booking: {}", e.getMessage(), e);
            return ExternalBookingResponse.builder()
                    .sourceSystem(request.getSourceSystem())
                    .externalBookingId(request.getExternalBookingId())
                    .message("Error creating booking: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    /**
     * Get booking status for external system
     */
    public ExternalBookingResponse getBookingStatus(int bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            return ExternalBookingResponse.builder()
                    .tripEaseBookingId(bookingId)
                    .message("Booking not found")
                    .success(false)
                    .build();
        }

        Booking booking = optionalBooking.get();
        return mapToResponse(booking);
    }

    /**
     * Get booking status by external booking ID
     */
    public ExternalBookingResponse getBookingStatusByExternalId(String sourceSystem, Long externalBookingId) {
        Optional<Booking> optionalBooking = bookingRepository
                .findBySourceSystemAndExternalBookingId(sourceSystem, externalBookingId);

        if (optionalBooking.isEmpty()) {
            return ExternalBookingResponse.builder()
                    .sourceSystem(sourceSystem)
                    .externalBookingId(externalBookingId)
                    .message("Booking not found")
                    .success(false)
                    .build();
        }

        return mapToResponse(optionalBooking.get());
    }

    /**
     * Cancel an external booking
     */
    public ExternalBookingResponse cancelExternalBooking(int bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            return ExternalBookingResponse.builder()
                    .tripEaseBookingId(bookingId)
                    .message("Booking not found")
                    .success(false)
                    .build();
        }

        Booking booking = optionalBooking.get();

        if (booking.getTripStatus() == TripStatus.IN_PROGRESS ||
                booking.getTripStatus() == TripStatus.COMPLETED) {
            return ExternalBookingResponse.builder()
                    .tripEaseBookingId(bookingId)
                    .status(booking.getTripStatus())
                    .message("Cannot cancel booking in current status: " + booking.getTripStatus())
                    .success(false)
                    .build();
        }

        booking.setTripStatus(TripStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("External booking {} cancelled", bookingId);

        return ExternalBookingResponse.builder()
                .tripEaseBookingId(bookingId)
                .sourceSystem(booking.getSourceSystem())
                .externalBookingId(booking.getExternalBookingId())
                .status(TripStatus.CANCELLED)
                .message("Booking cancelled successfully")
                .success(true)
                .build();
    }

    /**
     * Notify external system of booking status change (webhook)
     */
    public void notifyExternalSystem(Booking booking, String callbackUrl) {
        if (callbackUrl == null || callbackUrl.isEmpty()) {
            log.debug("No callback URL for booking {}, skipping notification", booking.getBookingId());
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-Key", apiKey);

            Map<String, Object> payload = new HashMap<>();
            payload.put("tripEaseBookingId", booking.getBookingId());
            payload.put("externalBookingId", booking.getExternalBookingId());
            payload.put("status", booking.getTripStatus().name());
            payload.put("driverEmail", booking.getDriverEmail());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(callbackUrl, request, String.class);

            log.info("Notified external system at {} for booking {}", callbackUrl, booking.getBookingId());
        } catch (Exception e) {
            log.error("Failed to notify external system: {}", e.getMessage());
        }
    }

    private ExternalBookingResponse mapToResponse(Booking booking) {
        return ExternalBookingResponse.builder()
                .tripEaseBookingId(booking.getBookingId())
                .sourceSystem(booking.getSourceSystem())
                .externalBookingId(booking.getExternalBookingId())
                .status(booking.getTripStatus())
                .driverEmail(booking.getDriverEmail())
                .estimatedFare(booking.getBillAmount())
                .estimatedTimeMin(booking.getEstimatedTimeMin())
                .message("Booking found")
                .success(true)
                .build();
    }
}
