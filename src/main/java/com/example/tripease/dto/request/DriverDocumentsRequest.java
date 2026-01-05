package com.example.tripease.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class DriverDocumentsRequest {
    private Integer driverId;

    @NotBlank(message = "Driving license number is required")
    @Pattern(regexp = "[A-Z]{2}[0-9]{13}", message = "Invalid Driving License Number")
    private String driverLicenseNumber;

    private LocalDate expiryDate;

    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$", message = "Invalid Aadhaar Number")
    private String aadhaarNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN Number")
    private String panCardNumber;
}
