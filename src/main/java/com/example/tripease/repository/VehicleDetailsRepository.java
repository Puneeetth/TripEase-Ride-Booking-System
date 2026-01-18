package com.example.tripease.repository;

import com.example.tripease.model.VehicleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, Long> {
    Optional<VehicleDetails> findByDriverDriverId(int driverId);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByInsuranceNumber(String insuranceNumber);

    boolean existsByRcNumber(String rcNumber);
}
