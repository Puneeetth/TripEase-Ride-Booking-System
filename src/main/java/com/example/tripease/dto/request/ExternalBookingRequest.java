package com.example.tripease.dto.request;

import com.example.tripease.Enum.RideType;
import lombok.*;

/**
 * Request DTO for external booking requests from other systems (e.g.,
 * Integrated Travel Management)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalBookingRequest {

    // Source system identifier (e.g., "ITM" for Integrated Travel Management)
    private String sourceSystem;

    // Booking ID in the source system for reference
    private Long externalBookingId;

    // Passenger details
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;

    // Location details
    private String pickupAddress;
    private Double pickupLat;
    private Double pickupLng;
    private String destinationAddress;
    private Double destinationLat;
    private Double destinationLng;

    // Trip details
    private Double tripDistanceInKm;
    private Integer estimatedTimeMin;
    private Double estimatedFare;
    private RideType rideType;

    // Optional special requests
    private String specialRequests;
}
