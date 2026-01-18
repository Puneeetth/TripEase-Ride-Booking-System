package com.example.tripease.repository;

import com.example.tripease.Enum.TripStatus;
import com.example.tripease.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Find pending bookings without a driver assigned (for driver to accept)
    List<Booking> findByTripStatusAndDriverIdIsNullOrderByBookedAtDesc(TripStatus tripStatus);

    // Find bookings by customer ID
    List<Booking> findByCustomerIdOrderByBookedAtDesc(int customerId);

    // Find bookings by driver ID
    List<Booking> findByDriverIdOrderByBookedAtDesc(int driverId);

    // Find by trip status
    List<Booking> findByTripStatus(TripStatus tripStatus);

    // Find by external booking ID (for integration with other systems)
    Optional<Booking> findBySourceSystemAndExternalBookingId(String sourceSystem, Long externalBookingId);

    // Find bookings by customer email
    List<Booking> findByCustomerEmailOrderByBookedAtDesc(String customerEmail);

    // Find bookings by driver email
    List<Booking> findByDriverEmailOrderByBookedAtDesc(String driverEmail);
}
