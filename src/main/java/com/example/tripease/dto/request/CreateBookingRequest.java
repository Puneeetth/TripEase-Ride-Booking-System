package com.example.tripease.dto.request;

import com.example.tripease.Enum.RideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
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
}
