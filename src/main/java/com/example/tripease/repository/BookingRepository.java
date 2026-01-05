package com.example.tripease.repository;

import com.example.tripease.Enum.TripStatus;
import com.example.tripease.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Find all pending bookings (for drivers to see)
    List<Booking> findByTripStatus(TripStatus tripStatus);

    // Find bookings for a specific customer
    List<Booking> findByCustomerIdOrderByBookedAtDesc(int customerId);

    // Find bookings for a specific driver
    List<Booking> findByDriverIdOrderByBookedAtDesc(int driverId);

    // Find pending bookings (not yet assigned to a driver)
    List<Booking> findByTripStatusAndDriverIdIsNullOrderByBookedAtDesc(TripStatus tripStatus);

    // Find external booking by source system and external ID
    Optional<Booking> findBySourceSystemAndExternalBookingId(String sourceSystem, Long externalBookingId);

    // Find all external bookings from a specific source system
    List<Booking> findBySourceSystemOrderByBookedAtDesc(String sourceSystem);
}
