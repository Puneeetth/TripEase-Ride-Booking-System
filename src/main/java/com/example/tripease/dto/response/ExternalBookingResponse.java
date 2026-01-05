package com.example.tripease.dto.response;

import com.example.tripease.Enum.TripStatus;
import lombok.*;

/**
 * Response DTO for external booking requests
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalBookingResponse {

    // TripEase internal booking ID
    private Integer tripEaseBookingId;

    // Reference back to source system
    private String sourceSystem;
    private Long externalBookingId;

    // Current status
    private TripStatus status;

    // Driver info (populated when assigned)
    private String driverName;
    private String driverPhone;
    private String driverEmail;

    // Trip details
    private Double estimatedFare;
    private Integer estimatedTimeMin;

    // Response message
    private String message;

    // Indicates if the request was successful
    private boolean success;
}
