package com.example.tripease.controller;

import com.example.tripease.dto.request.ExternalBookingRequest;
import com.example.tripease.dto.response.ExternalBookingResponse;
import com.example.tripease.service.ExternalRideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling external booking requests from other systems (e.g.,
 * Integrated Travel Management)
 * These endpoints use API key authentication instead of JWT
 */
@RestController
@RequestMapping("/api/external/ride")
@RequiredArgsConstructor
@Slf4j
public class ExternalRideController {

    private final ExternalRideService externalRideService;

    @Value("${integration.api.key}")
    private String expectedApiKey;

    /**
     * Create a new booking from an external system
     */
    @PostMapping("/book")
    public ResponseEntity<ExternalBookingResponse> createExternalBooking(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @RequestBody ExternalBookingRequest request) {

        // Validate API key
        if (!isValidApiKey(apiKey)) {
            log.warn("Invalid API key attempt from source: {}", request.getSourceSystem());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ExternalBookingResponse.builder()
                            .message("Invalid or missing API key")
                            .success(false)
                            .build());
        }

        log.info("Received external booking request from {} for external ID: {}",
                request.getSourceSystem(), request.getExternalBookingId());

        ExternalBookingResponse response = externalRideService.createExternalBooking(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get booking status by TripEase booking ID
     */
    @GetMapping("/{bookingId}/status")
    public ResponseEntity<ExternalBookingResponse> getBookingStatus(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @PathVariable int bookingId) {

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ExternalBookingResponse.builder()
                            .message("Invalid or missing API key")
                            .success(false)
                            .build());
        }

        ExternalBookingResponse response = externalRideService.getBookingStatus(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get booking status by external booking ID
     */
    @GetMapping("/external/{sourceSystem}/{externalBookingId}/status")
    public ResponseEntity<ExternalBookingResponse> getBookingStatusByExternalId(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @PathVariable String sourceSystem,
            @PathVariable Long externalBookingId) {

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ExternalBookingResponse.builder()
                            .message("Invalid or missing API key")
                            .success(false)
                            .build());
        }

        ExternalBookingResponse response = externalRideService.getBookingStatusByExternalId(
                sourceSystem, externalBookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an external booking
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ExternalBookingResponse> cancelBooking(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @PathVariable int bookingId) {

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ExternalBookingResponse.builder()
                            .message("Invalid or missing API key")
                            .success(false)
                            .build());
        }

        log.info("Received cancel request for booking: {}", bookingId);
        ExternalBookingResponse response = externalRideService.cancelExternalBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check for external integration
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing API key");
        }

        return ResponseEntity.ok("TripEase External API is running");
    }

    private boolean isValidApiKey(String apiKey) {
        return apiKey != null && apiKey.equals(expectedApiKey);
    }
}
