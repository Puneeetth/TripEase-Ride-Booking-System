package com.example.tripease.dto.response;

import com.example.tripease.Enum.RideType;
import com.example.tripease.Enum.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsResponse {
    private int bookingId;
    private int customerId;
    private String customerEmail;
    private Integer driverId;
    private String driverEmail;
    private String pickupAddress;
    private double pickupLat;
    private double pickupLng;
    private String destinationAddress;
    private double destinationLat;
    private double destinationLng;
    private double tripDistanceInKm;
    private int estimatedTimeMin;
    private double billAmount;
    private RideType rideType;
    private TripStatus tripStatus;
    private Date bookedAt;
    private String message;
}
