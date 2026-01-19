package com.example.tripease.service;

import com.example.tripease.Enum.DocumentStatus;
import com.example.tripease.Enum.VehicleType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DriverService.
 * Uses Mockito to isolate the service layer from repositories.
 */
@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

        @Mock
        private DriverRepository driverRepository;

        @Mock
        private DriverDocumentsRepository driverDocumentsRepository;

        @Mock
        private VehicleDetailsRepository vehicleDetailsRepository;

        @InjectMocks
        private DriverService driverService;

        // Test data
        private Driver testDriver;
        private DriverRequest testDriverRequest;
        private DriverDocumentsRequest testDocumentsRequest;
        private VehicleDetailsRequest testVehicleRequest;
        private DriverDocuments testDriverDocuments;
        private VehicleDetails testVehicleDetails;

        @BeforeEach
        void setUp() {
                // Setup test driver
                testDriver = Driver.builder()
                                .driverId(1)
                                .name("John Doe")
                                .age(30)
                                .emailId("john.doe@example.com")
                                .build();

                // Setup driver request
                testDriverRequest = DriverRequest.builder()
                                .name("John Doe")
                                .age(30)
                                .emailId("john.doe@example.com")
                                .build();

                // Setup documents request
                testDocumentsRequest = DriverDocumentsRequest.builder()
                                .driverId(1)
                                .driverLicenseNumber("KA1234567890123")
                                .expiryDate(LocalDate.now().plusYears(5))
                                .aadhaarNumber("234567890123")
                                .panCardNumber("ABCDE1234F")
                                .build();

                // Setup driver documents
                testDriverDocuments = DriverDocuments.builder()
                                .documentId(1L)
                                .driverLicenseNumber("KA1234567890123")
                                .expiryDate(LocalDate.now().plusYears(5))
                                .aadhaarNumber("234567890123")
                                .panCardNumber("ABCDE1234F")
                                .documentStatus(DocumentStatus.PENDING)
                                .driver(testDriver)
                                .build();

                // Setup vehicle request
                testVehicleRequest = VehicleDetailsRequest.builder()
                                .driverId(1)
                                .vehicleType(VehicleType.CAR)
                                .registrationNumber("KA01AB1234")
                                .insuranceNumber("INS123456789")
                                .insuranceExpiryDate(LocalDate.now().plusYears(1))
                                .rcNumber("RC123456789")
                                .build();

                // Setup vehicle details
                testVehicleDetails = VehicleDetails.builder()
                                .vehicleId(1L)
                                .vehicleType(VehicleType.CAR)
                                .registrationNumber("KA01AB1234")
                                .insuranceNumber("INS123456789")
                                .insuranceExpiryDate(LocalDate.now().plusYears(1))
                                .rcNumber("RC123456789")
                                .driver(testDriver)
                                .build();
        }
}

// ==================== addDriver Tests ====================
@Nested
@DisplayName("addDriver Tests")
class AddDriverTests {

        @Test
        @DisplayName("Should successfully add a new driver")
        void addDriver_Success() {
                // Arrange
                when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

                // Act
                DriverResponse response = driverService.addDriver(testDriverRequest);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getDriverId()).isEqualTo(1);
                assertThat(response.getName()).isEqualTo("John Doe");
                assertThat(response.getAge()).isEqualTo(30);
                assertThat(response.getEmailId()).isEqualTo("john.doe@example.com");
                verify(driverRepository, times(1)).save(any(Driver.class));
        }

        @Test
        @DisplayName("Should add driver with minimum age (18)")
        void addDriver_MinimumAge() {
                // Arrange
                DriverRequest minAgeRequest = DriverRequest.builder()
                                .name("Young Driver")
                                .age(18)
                                .emailId("young@example.com")
                                .build();

                Driver minAgeDriver = Driver.builder()
                                .driverId(2)
                                .name("Young Driver")
                                .age(18)
                                .emailId("young@example.com")
                                .build();

                when(driverRepository.save(any(Driver.class))).thenReturn(minAgeDriver);

                // Act
                DriverResponse response = driverService.addDriver(minAgeRequest);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getAge()).isEqualTo(18);
        }
}

// ==================== getDriver Tests ====================
@Nested
@DisplayName("getDriver Tests")
class GetDriverTests {

        @Test
        @DisplayName("Should return driver when found")
        void getDriver_Success() {
                // Arrange
                when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));

                // Act
                DriverResponse response = driverService.getDriver(1);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getDriverId()).isEqualTo(1);
                assertThat(response.getName()).isEqualTo("John Doe");
                verify(driverRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("Should throw DriverNotFoundException when driver not found")
        void getDriver_NotFound() {
                // Arrange
                when(driverRepository.findById(anyInt())).thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> driverService.getDriver(999))
                                .isInstanceOf(DriverNotFoundException.class)
                                .hasMessageContaining("Driver not found with ID: 999");
                verify(driverRepository, times(1)).findById(999);
        }
}

// // ==================== submitDocuments Tests ====================
// @Nested
// @DisplayName("submitDocuments Tests")
// class SubmitDocumentsTests {

// @Test
// @DisplayName("Should successfully submit documents for driver")
// void submitDocuments_Success() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(driverDocumentsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(driverDocumentsRepository.existsByDriverLicenseNumber(anyString())).thenReturn(false);
// when(driverDocumentsRepository.existsByAadhaarNumber(anyString())).thenReturn(false);
// when(driverDocumentsRepository.existsByPanCardNumber(anyString())).thenReturn(false);
// when(driverDocumentsRepository.save(any(DriverDocuments.class))).thenReturn(testDriverDocuments);

// // Act
// DriverDocumentsResponse response =
// driverService.submitDocuments(testDocumentsRequest);

// // Assert
// assertThat(response).isNotNull();
// assertThat(response.getDocumentId()).isEqualTo(1L);
// verify(driverDocumentsRepository, times(1)).save(any(DriverDocuments.class));
// }

// @Test
// @DisplayName("Should throw DriverNotFoundException when driver not found")
// void submitDocuments_DriverNotFound() {
// // Arrange
// when(driverRepository.findById(anyInt())).thenReturn(Optional.empty());

// // Act & Assert
// assertThatThrownBy(() -> driverService.submitDocuments(testDocumentsRequest))
// .isInstanceOf(DriverNotFoundException.class)
// .hasMessageContaining("Driver not found with ID: 1");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException when documents already
// submitted")
// void submitDocuments_AlreadySubmitted() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(driverDocumentsRepository.findByDriverDriverId(1)).thenReturn(Optional.of(testDriverDocuments));

// // Act & Assert
// assertThatThrownBy(() -> driverService.submitDocuments(testDocumentsRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("Documents already submitted for this driver");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException for duplicate license
// number")
// void submitDocuments_DuplicateLicenseNumber() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(driverDocumentsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(driverDocumentsRepository.existsByDriverLicenseNumber(anyString())).thenReturn(true);

// // Act & Assert
// assertThatThrownBy(() -> driverService.submitDocuments(testDocumentsRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("Driving License Number already registered");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException for duplicate Aadhaar
// number")
// void submitDocuments_DuplicateAadhaarNumber() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(driverDocumentsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(driverDocumentsRepository.existsByDriverLicenseNumber(anyString())).thenReturn(false);
// when(driverDocumentsRepository.existsByAadhaarNumber(anyString())).thenReturn(true);

// // Act & Assert
// assertThatThrownBy(() -> driverService.submitDocuments(testDocumentsRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("Aadhaar Number already registered");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException for duplicate PAN card
// number")
// void submitDocuments_DuplicatePanCardNumber() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(driverDocumentsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(driverDocumentsRepository.existsByDriverLicenseNumber(anyString())).thenReturn(false);
// when(driverDocumentsRepository.existsByAadhaarNumber(anyString())).thenReturn(false);
// when(driverDocumentsRepository.existsByPanCardNumber(anyString())).thenReturn(true);

// // Act & Assert
// assertThatThrownBy(() -> driverService.submitDocuments(testDocumentsRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("PAN Card Number already registered");
// }
// }

// // ==================== getDocumentStatus Tests ====================
// @Nested
// @DisplayName("getDocumentStatus Tests")
// class GetDocumentStatusTests {

// @Test
// @DisplayName("Should return document status when found")
// void getDocumentStatus_Success() {
// // Arrange
// when(driverDocumentsRepository.findByDriverDriverId(1)).thenReturn(Optional.of(testDriverDocuments));

// // Act
// DriverDocumentsResponse response = driverService.getDocumentStatus(1);

// // Assert
// assertThat(response).isNotNull();
// assertThat(response.getDocumentStatus()).isEqualTo(DocumentStatus.PENDING);
// verify(driverDocumentsRepository, times(1)).findByDriverDriverId(1);
// }

// @Test
// @DisplayName("Should throw ResourceNotFoundException when no documents
// found")
// void getDocumentStatus_NotFound() {
// // Arrange
// when(driverDocumentsRepository.findByDriverDriverId(anyInt())).thenReturn(Optional.empty());

// // Act & Assert
// assertThatThrownBy(() -> driverService.getDocumentStatus(999))
// .isInstanceOf(ResourceNotFoundException.class)
// .hasMessageContaining("No documents found for driver ID: 999");
// }
// }

// // ==================== submitVehicleDetails Tests ====================
// @Nested
// @DisplayName("submitVehicleDetails Tests")
// class SubmitVehicleDetailsTests {

// @Test
// @DisplayName("Should successfully submit vehicle details")
// void submitVehicleDetails_Success() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(vehicleDetailsRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByInsuranceNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByRcNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.save(any(VehicleDetails.class))).thenReturn(testVehicleDetails);

// // Act
// VehicleDetailsResponse response =
// driverService.submitVehicleDetails(testVehicleRequest);

// // Assert
// assertThat(response).isNotNull();
// assertThat(response.getVehicleId()).isEqualTo(1L);
// verify(vehicleDetailsRepository, times(1)).save(any(VehicleDetails.class));
// }

// @Test
// @DisplayName("Should throw DriverNotFoundException when driver not found")
// void submitVehicleDetails_DriverNotFound() {
// // Arrange
// when(driverRepository.findById(anyInt())).thenReturn(Optional.empty());

// // Act & Assert
// assertThatThrownBy(() ->
// driverService.submitVehicleDetails(testVehicleRequest))
// .isInstanceOf(DriverNotFoundException.class)
// .hasMessageContaining("Driver not found with ID: 1");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException when vehicle already
// submitted")
// void submitVehicleDetails_AlreadySubmitted() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.of(testVehicleDetails));

// // Act & Assert
// assertThatThrownBy(() ->
// driverService.submitVehicleDetails(testVehicleRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("Vehicle details already submitted for this driver");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException for duplicate
// registration number")
// void submitVehicleDetails_DuplicateRegistrationNumber() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(vehicleDetailsRepository.existsByRegistrationNumber(anyString())).thenReturn(true);

// // Act & Assert
// assertThatThrownBy(() ->
// driverService.submitVehicleDetails(testVehicleRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("Registration Number already registered");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException for duplicate insurance
// number")
// void submitVehicleDetails_DuplicateInsuranceNumber() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(vehicleDetailsRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByInsuranceNumber(anyString())).thenReturn(true);

// // Act & Assert
// assertThatThrownBy(() ->
// driverService.submitVehicleDetails(testVehicleRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("Insurance Number already registered");
// }

// @Test
// @DisplayName("Should throw DuplicateResourceException for duplicate RC
// number")
// void submitVehicleDetails_DuplicateRcNumber() {
// // Arrange
// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(vehicleDetailsRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByInsuranceNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByRcNumber(anyString())).thenReturn(true);

// // Act & Assert
// assertThatThrownBy(() ->
// driverService.submitVehicleDetails(testVehicleRequest))
// .isInstanceOf(DuplicateResourceException.class)
// .hasMessageContaining("RC Number already registered");
// }

// @Test
// @DisplayName("Should handle different vehicle types")
// void submitVehicleDetails_DifferentVehicleTypes() {
// // Arrange - Test with BIKE
// VehicleDetailsRequest bikeRequest = VehicleDetailsRequest.builder()
// .driverId(1)
// .vehicleType(VehicleType.BIKE)
// .registrationNumber("KA01CD5678")
// .insuranceNumber("INS987654321")
// .insuranceExpiryDate(LocalDate.now().plusYears(1))
// .rcNumber("RC987654321")
// .build();

// VehicleDetails bikeDetails = VehicleDetails.builder()
// .vehicleId(2L)
// .vehicleType(VehicleType.BIKE)
// .registrationNumber("KA01CD5678")
// .insuranceNumber("INS987654321")
// .insuranceExpiryDate(LocalDate.now().plusYears(1))
// .rcNumber("RC987654321")
// .driver(testDriver)
// .build();

// when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.empty());
// when(vehicleDetailsRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByInsuranceNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.existsByRcNumber(anyString())).thenReturn(false);
// when(vehicleDetailsRepository.save(any(VehicleDetails.class))).thenReturn(bikeDetails);

// // Act
// VehicleDetailsResponse response =
// driverService.submitVehicleDetails(bikeRequest);

// // Assert
// assertThat(response).isNotNull();
// assertThat(response.getVehicleType()).isEqualTo(VehicleType.BIKE);
// }
// }

// // ==================== getVehicleDetails Tests ====================
// @Nested
// @DisplayName("getVehicleDetails Tests")
// class GetVehicleDetailsTests {

// @Test
// @DisplayName("Should return vehicle details when found")
// void getVehicleDetails_Success() {
// // Arrange
// when(vehicleDetailsRepository.findByDriverDriverId(1)).thenReturn(Optional.of(testVehicleDetails));

// // Act
// VehicleDetailsResponse response = driverService.getVehicleDetails(1);

// // Assert
// assertThat(response).isNotNull();
// assertThat(response.getVehicleId()).isEqualTo(1L);
// assertThat(response.getVehicleType()).isEqualTo(VehicleType.CAR);
// assertThat(response.getRegistrationNumber()).isEqualTo("KA01AB1234");
// verify(vehicleDetailsRepository, times(1)).findByDriverDriverId(1);
// }

// @Test
// @DisplayName("Should throw ResourceNotFoundException when no vehicle details
// found")
// void getVehicleDetails_NotFound() {
// // Arrange
// when(vehicleDetailsRepository.findByDriverDriverId(anyInt())).thenReturn(Optional.empty());

// // Act & Assert
// assertThatThrownBy(() -> driverService.getVehicleDetails(999))
// .isInstanceOf(ResourceNotFoundException.class)
// .hasMessageContaining("No vehicle details found for driver ID: 999");
// }
// }
// }
