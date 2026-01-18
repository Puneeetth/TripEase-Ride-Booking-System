package com.example.tripease.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DriverDocumentRequest {
    @NotBlank
    @Pattern(regexp = "[A-Z]{2}[0-9]{13}", message = "Invalid Driving License Number")
    private String driverLicenseNumber;
    @NotBlank
    private LocalDate expiryDate;
    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$", message = "Invalid Aadhaar Number")
    private String AadhaarNumber;
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid Pan Number")
    private String PanCardNumber;
}
