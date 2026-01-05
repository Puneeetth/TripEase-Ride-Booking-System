package com.example.tripease.service;

import com.example.tripease.Enum.DocumentStatus;
import com.example.tripease.dto.request.DriverDocumentsRequest;
import com.example.tripease.dto.request.DriverRequest;
import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.dto.response.DriverResponse;
import com.example.tripease.exception.DriverNotFoundException;
import com.example.tripease.model.Driver;
import com.example.tripease.model.DriverDocuments;
import com.example.tripease.repository.DriverDocumentsRepository;
import com.example.tripease.repository.DriverRepository;
import com.example.tripease.transformer.DriverTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DriverDocumentsRepository driverDocumentsRepository;

    public DriverResponse addDriver(DriverRequest driverRequest) {
        Driver driver = DriverTransformer.driverRequestToDriver(driverRequest);
        Driver savedDriver = driverRepository.save(driver);
        return DriverTransformer.driverToDriverResponse(savedDriver);
    }

    public DriverResponse getDriver(int driverId) {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (optionalDriver.isEmpty()) {
            throw new DriverNotFoundException("Invalid Driver Id");
        }
        Driver savedDriver = optionalDriver.get();
        return DriverTransformer.driverToDriverResponse(savedDriver);
    }

    @Transactional
    public DriverDocumentsResponse submitDocuments(DriverDocumentsRequest request) {
        Optional<Driver> optionalDriver = driverRepository.findById(request.getDriverId());
        if (optionalDriver.isEmpty()) {
            return DriverDocumentsResponse.builder()
                    .message("Driver not found")
                    .build();
        }

        Driver driver = optionalDriver.get();

        // Check if driver already submitted documents
        Optional<DriverDocuments> existingDocs = driverDocumentsRepository.findByDriverDriverId(request.getDriverId());
        if (existingDocs.isPresent()) {
            return DriverDocumentsResponse.builder()
                    .documentId(existingDocs.get().getDocumentId())
                    .documentStatus(existingDocs.get().getDocumentStatus())
                    .message("Documents already submitted")
                    .build();
        }

        // Check if driving license already exists
        if (driverDocumentsRepository.existsByDriverLicenseNumber(request.getDriverLicenseNumber())) {
            return DriverDocumentsResponse.builder()
                    .message("Driving License Number already registered with another driver")
                    .build();
        }

        // Check if Aadhaar already exists
        if (driverDocumentsRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            return DriverDocumentsResponse.builder()
                    .message("Aadhaar Number already registered with another driver")
                    .build();
        }

        // Check if PAN already exists
        if (driverDocumentsRepository.existsByPanCardNumber(request.getPanCardNumber())) {
            return DriverDocumentsResponse.builder()
                    .message("PAN Card Number already registered with another driver")
                    .build();
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

        return DriverDocumentsResponse.builder()
                .documentId(savedDocs.getDocumentId())
                .documentStatus(savedDocs.getDocumentStatus())
                .message("Documents submitted successfully. Verification pending.")
                .build();
    }

    public DriverDocumentsResponse getDocumentStatus(int driverId) {
        Optional<DriverDocuments> optionalDocs = driverDocumentsRepository.findByDriverDriverId(driverId);
        if (optionalDocs.isEmpty()) {
            return DriverDocumentsResponse.builder()
                    .message("No documents found for this driver")
                    .build();
        }

        DriverDocuments docs = optionalDocs.get();
        return DriverDocumentsResponse.builder()
                .documentId(docs.getDocumentId())
                .documentStatus(docs.getDocumentStatus())
                .rejectedReason(docs.getRejectedReason())
                .message("Document status retrieved successfully")
                .build();
    }
}
