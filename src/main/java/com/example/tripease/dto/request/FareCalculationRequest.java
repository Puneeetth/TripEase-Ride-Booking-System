package com.example.tripease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareCalculationRequest {
    private double pickupLat;
    private double pickupLng;
    private String pickupAddress;
    private double destinationLat;
    private double destinationLng;
    private String destinationAddress;
}
