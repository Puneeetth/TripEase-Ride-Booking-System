package com.example.tripease.dto.response;

import com.example.tripease.Enum.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetailsResponse {
    private Long vehicleId;
    private VehicleType vehicleType;
    private String registrationNumber;
    private String message;
}
