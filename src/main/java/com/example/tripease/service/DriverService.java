package com.example.tripease.service;

import com.example.tripease.Enum.DocumentStatus;
import com.example.tripease.dto.request.DriverDocumentsRequest;
import com.example.tripease.dto.request.DriverRequest;
import com.example.tripease.dto.request.VehicleDetailsRequest;
import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.dto.response.DriverResponse;
import com.example.tripease.dto.response.VehicleDetailsResponse;
import com.example.tripease.exception.DriverNotFoundException;
import com.example.tripease.exception.DuplicateResourceException;
import com.example.tripease.exception.ResourceNotFoundException;
import com.example.tripease.model.Driver;
import com.example.tripease.model.DriverDocuments;
import com.example.tripease.model.VehicleDetails;
import com.example.tripease.repository.DriverDocumentsRepository;
import com.example.tripease.repository.DriverRepository;
import com.example.tripease.repository.VehicleDetailsRepository;
import com.example.tripease.transformer.DriverDocumentsTransformer;
import com.example.tripease.transformer.DriverTransformer;
import com.example.tripease.transformer.VehicleDetailsTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class DriverService {
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DriverDocumentsRepository driverDocumentsRepository;

    @Autowired
    VehicleDetailsRepository vehicleDetailsRepository;

    public DriverResponse addDriver(DriverRequest driverRequest) {
        Driver driver = DriverTransformer.driverRequestToDriver(driverRequest);
        Driver savedDriver = driverRepository.save(driver);
        return DriverTransformer.driverToDriverResponse(savedDriver);
    }

    public DriverResponse getDriver(int driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + driverId));
        return DriverTransformer.driverToDriverResponse(driver);
    }

    @Transactional
    public DriverDocumentsResponse submitDocuments(DriverDocumentsRequest request) {
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + request.getDriverId()));

        // Check if driver already submitted documents
        Optional<DriverDocuments> existingDocs = driverDocumentsRepository.findByDriverDriverId(request.getDriverId());
        if (existingDocs.isPresent()) {
            throw new DuplicateResourceException("Documents already submitted for this driver");
        }

        // Check for duplicate documents
        if (driverDocumentsRepository.existsByDriverLicenseNumber(request.getDriverLicenseNumber())) {
            throw new DuplicateResourceException("Driving License Number already registered with another driver");
        }
        if (driverDocumentsRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new DuplicateResourceException("Aadhaar Number already registered with another driver");
        }
        if (driverDocumentsRepository.existsByPanCardNumber(request.getPanCardNumber())) {
            throw new DuplicateResourceException("PAN Card Number already registered with another driver");
        }

        // Create new documents
        DriverDocuments documents = DriverDocuments.builder()
                .driverLicenseNumber(request.getDriverLicenseNumber())
                .expiryDate(request.getExpiryDate())
                .aadhaarNumber(request.getAadhaarNumber())
                .panCardNumber(request.getPanCardNumber())
                .documentStatus(DocumentStatus.PENDING)
                .driver(driver)
                .build();

        DriverDocuments savedDocs = driverDocumentsRepository.save(documents);
        return DriverDocumentsTransformer.toSubmissionSuccess(savedDocs);
    }

    public DriverDocumentsResponse getDocumentStatus(int driverId) {
        DriverDocuments docs = driverDocumentsRepository.findByDriverDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("No documents found for driver ID: " + driverId));
        return DriverDocumentsTransformer.toStatusResponse(docs);
    }

    @Transactional
    public VehicleDetailsResponse submitVehicleDetails(VehicleDetailsRequest request) {
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + request.getDriverId()));

        // Check if driver already submitted vehicle details
        Optional<VehicleDetails> existingVehicle = vehicleDetailsRepository.findByDriverDriverId(request.getDriverId());
        if (existingVehicle.isPresent()) {
            throw new DuplicateResourceException("Vehicle details already submitted for this driver");
        }

        // Check for duplicate vehicle details
        if (vehicleDetailsRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Registration Number already registered with another driver");
        }
        if (vehicleDetailsRepository.existsByInsuranceNumber(request.getInsuranceNumber())) {
            throw new DuplicateResourceException("Insurance Number already registered with another driver");
        }
        if (vehicleDetailsRepository.existsByRcNumber(request.getRcNumber())) {
            throw new DuplicateResourceException("RC Number already registered with another driver");
        }

        // Create and save vehicle details using transformer
        VehicleDetails vehicleDetails = VehicleDetailsTransformer.toEntity(request, driver);
        VehicleDetails savedVehicle = vehicleDetailsRepository.save(vehicleDetails);
        return VehicleDetailsTransformer.toSubmissionSuccess(savedVehicle);
    }

    public VehicleDetailsResponse getVehicleDetails(int driverId) {
        VehicleDetails vehicle = vehicleDetailsRepository.findByDriverDriverId(driverId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No vehicle details found for driver ID: " + driverId));
        return VehicleDetailsTransformer.toResponse(vehicle);
    }
}
