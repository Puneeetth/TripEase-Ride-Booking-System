package com.example.tripease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareCalculationResponse {
    private String pickupAddress;
    private String destinationAddress;
    private double distanceKm;
    private String distanceText;
    private int durationMin;
    private String durationText;
    private List<FareEstimate> fareEstimates;
    private String message;
}
