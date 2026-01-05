package com.example.tripease.controller;

import com.example.tripease.dto.request.DriverDocumentsRequest;
import com.example.tripease.dto.request.DriverRequest;
import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.dto.response.DriverResponse;
import com.example.tripease.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {
  @Autowired
  DriverService driverService;

  @PostMapping("/add")
  public DriverResponse addDriver(@RequestBody DriverRequest driverRequest) {
    return driverService.addDriver(driverRequest);
  }

  @GetMapping("/get/driver-id/{id}")
  public DriverResponse getDriver(@PathVariable("id") int driverId) {
    return driverService.getDriver(driverId);
  }

  @PostMapping("/documents/submit")
  public ResponseEntity<DriverDocumentsResponse> submitDocuments(@RequestBody DriverDocumentsRequest request) {
    DriverDocumentsResponse response = driverService.submitDocuments(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/documents/status/{driverId}")
  public ResponseEntity<DriverDocumentsResponse> getDocumentStatus(@PathVariable("driverId") int driverId) {
    DriverDocumentsResponse response = driverService.getDocumentStatus(driverId);
    return ResponseEntity.ok(response);
  }
}
