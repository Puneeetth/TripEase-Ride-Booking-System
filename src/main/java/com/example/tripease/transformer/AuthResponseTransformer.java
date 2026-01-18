package com.example.tripease.transformer;

import com.example.tripease.dto.response.AuthResponse;
import com.example.tripease.model.Driver;
import com.example.tripease.model.User;

/**
 * Transformer for authentication response DTOs.
 * Centralizes all AuthResponse construction logic.
 */
public class AuthResponseTransformer {

    private AuthResponseTransformer() {
        // Private constructor to prevent instantiation
    }

    /**
     * Build success response for driver registration
     */
    public static AuthResponse toDriverRegistrationSuccess(User user, Driver driver, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(driver.getName())
                .role(user.getRole())
                .referenceId(driver.getDriverId())
                .message("Driver registered successfully")
                .build();
    }

    /**
     * Build success response for driver login
     */
    public static AuthResponse toDriverLoginSuccess(User user, String driverName, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(driverName)
                .role(user.getRole())
                .referenceId(user.getReferenceId())
                .message("Driver login successful")
                .build();
    }

    /**
     * Build success response for customer registration
     */
    public static AuthResponse toCustomerRegistrationSuccess(User user, Integer customerId, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .referenceId(customerId)
                .message("Customer registered successfully")
                .build();
    }

    /**
     * Build success response for customer login
     */
    public static AuthResponse toCustomerLoginSuccess(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .referenceId(user.getReferenceId())
                .message("Customer login successful")
                .build();
    }

    /**
     * Build success response for validator login
     */
    public static AuthResponse toValidatorLoginSuccess(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .referenceId(user.getReferenceId())
                .message("Validator login successful")
                .build();
    }

    /**
     * Build success response for validator registration
     */
    public static AuthResponse toValidatorRegistrationSuccess() {
        return AuthResponse.builder()
                .message("Validator registered successfully. You can now login.")
                .build();
    }
}
