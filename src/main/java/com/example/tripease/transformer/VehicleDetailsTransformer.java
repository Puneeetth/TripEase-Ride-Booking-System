package com.example.tripease.transformer;

import com.example.tripease.dto.request.VehicleDetailsRequest;
import com.example.tripease.dto.response.VehicleDetailsResponse;
import com.example.tripease.model.Driver;
import com.example.tripease.model.VehicleDetails;

/**
 * Transformer for vehicle details DTOs.
 * Centralizes all VehicleDetails conversion logic.
 */
public class VehicleDetailsTransformer {

    private VehicleDetailsTransformer() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert request DTO to entity
     */
    public static VehicleDetails toEntity(VehicleDetailsRequest request, Driver driver) {
        return VehicleDetails.builder()
                .vehicleType(request.getVehicleType())
                .registrationNumber(request.getRegistrationNumber())
                .insuranceNumber(request.getInsuranceNumber())
                .insuranceExpiryDate(request.getInsuranceExpiryDate())
                .rcNumber(request.getRcNumber())
                .driver(driver)
                .build();
    }

    /**
     * Build response for successful vehicle submission
     */
    public static VehicleDetailsResponse toSubmissionSuccess(VehicleDetails vehicle) {
        return VehicleDetailsResponse.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleType(vehicle.getVehicleType())
                .registrationNumber(vehicle.getRegistrationNumber())
                .message("Vehicle details submitted successfully")
                .build();
    }

    /**
     * Build response for vehicle details retrieval
     */
    public static VehicleDetailsResponse toResponse(VehicleDetails vehicle) {
        return VehicleDetailsResponse.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleType(vehicle.getVehicleType())
                .registrationNumber(vehicle.getRegistrationNumber())
                .message("Vehicle details retrieved successfully")
                .build();
    }
}
