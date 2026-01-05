package com.example.tripease.model;

import com.example.tripease.Enum.RideType;
import com.example.tripease.Enum.TripStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;

    // Customer info
    private int customerId;
    private String customerEmail;

    // Driver info (assigned when driver accepts)
    private Integer driverId;
    private String driverEmail;

    // Location info
    private String pickupAddress;
    private double pickupLat;
    private double pickupLng;
    private String destinationAddress;
    private double destinationLat;
    private double destinationLng;

    // Trip details
    private double tripDistanceInKm;
    private int estimatedTimeMin;
    private double billAmount;

    @Enumerated(EnumType.STRING)
    private RideType rideType;

    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus;

    @CreationTimestamp
    private Date bookedAt;

    @UpdateTimestamp
    private Date lastUpdatedAt;

    // External booking tracking (for bookings from other systems like ITM)
    private String sourceSystem; // e.g., "ITM" for Integrated Travel Management
    private Long externalBookingId; // ID in the source system

    @Builder.Default
    private Boolean isExternalBooking = false;

    // Additional passenger info for external bookings
    private String passengerName;
    private String passengerPhone;
    private String specialRequests;

    // Callback URL for notifying external system of status changes
    private String callbackUrl;
}
