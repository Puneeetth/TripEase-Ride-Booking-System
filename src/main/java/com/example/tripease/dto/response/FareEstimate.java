package com.example.tripease.dto.response;

import com.example.tripease.Enum.RideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimate {
    private RideType rideType;
    private String rideName;
    private String rideIcon;
    private int baseFare;
    private int distanceFare;
    private int timeFare;
    private int totalFare;
    private double distanceKm;
    private String distanceText;
    private int durationMin;
    private String durationText;
}
