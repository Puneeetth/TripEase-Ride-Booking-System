package com.example.tripease.repository;

import com.example.tripease.model.DriverDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverDocumentsRepository extends JpaRepository<DriverDocuments, Long> {
    Optional<DriverDocuments> findByDriverDriverId(int driverId);

    boolean existsByDriverLicenseNumber(String driverLicenseNumber);

    boolean existsByAadhaarNumber(String aadhaarNumber);

    boolean existsByPanCardNumber(String panCardNumber);
}
