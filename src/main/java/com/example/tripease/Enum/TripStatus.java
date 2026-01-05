package com.example.tripease.Enum;

public enum TripStatus {
    PENDING, // Customer created booking, waiting for driver
    ACCEPTED, // Driver accepted the booking
    REJECTED, // Driver rejected the booking
    IN_PROGRESS, // Trip is ongoing
    COMPLETED, // Trip completed
    CANCELLED // Customer cancelled
}
