package com.example.tripease.controller;

import com.example.tripease.dto.request.DriverDocumentsRequest;
import com.example.tripease.dto.request.DriverRequest;
import com.example.tripease.dto.request.VehicleDetailsRequest;
import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.dto.response.DriverResponse;
import com.example.tripease.dto.response.VehicleDetailsResponse;
import com.example.tripease.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

  private final DriverService driverService;

  @PostMapping("/add")
  public ResponseEntity<DriverResponse> addDriver(@Valid @RequestBody DriverRequest driverRequest) {
    return ResponseEntity.ok(driverService.addDriver(driverRequest));
  }

  @GetMapping("/get/driver-id/{id}")
  public ResponseEntity<DriverResponse> getDriver(@PathVariable("id") int driverId) {
    return ResponseEntity.ok(driverService.getDriver(driverId));
  }

  @PostMapping("/documents/submit")
  public ResponseEntity<DriverDocumentsResponse> submitDocuments(@Valid @RequestBody DriverDocumentsRequest request) {
    return ResponseEntity.ok(driverService.submitDocuments(request));
  }

  @GetMapping("/documents/status/{driverId}")
  public ResponseEntity<DriverDocumentsResponse> getDocumentStatus(@PathVariable("driverId") int driverId) {
    return ResponseEntity.ok(driverService.getDocumentStatus(driverId));
  }

  @PostMapping("/vehicle/submit")
  public ResponseEntity<VehicleDetailsResponse> submitVehicleDetails(
      @Valid @RequestBody VehicleDetailsRequest request) {
    return ResponseEntity.ok(driverService.submitVehicleDetails(request));
  }

  @GetMapping("/vehicle/{driverId}")
  public ResponseEntity<VehicleDetailsResponse> getVehicleDetails(@PathVariable("driverId") int driverId) {
    return ResponseEntity.ok(driverService.getVehicleDetails(driverId));
  }
}
