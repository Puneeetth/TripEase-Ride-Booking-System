package com.example.tripease.dto.request;

import com.example.tripease.Enum.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetailsRequest {
    private Integer driverId;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$", message = "Invalid Registration Number")
    private String registrationNumber;

    @NotBlank(message = "Insurance number is required")
    private String insuranceNumber;

    @NotNull(message = "Insurance expiry date is required")
    private LocalDate insuranceExpiryDate;

    @NotBlank(message = "RC number is required")
    private String rcNumber;
}
