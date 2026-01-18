package com.example.tripease.controller;

import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/validator")
public class ValidatorController {

    @Autowired
    private ValidatorService validatorService;

    @GetMapping("/documents/pending")
    public ResponseEntity<List<DriverDocumentsResponse>> getPendingDocuments() {
        List<DriverDocumentsResponse> pendingDocs = validatorService.getPendingDocuments();
        return ResponseEntity.ok(pendingDocs);
    }

    @PostMapping("/documents/approve/{driverId}")
    public ResponseEntity<DriverDocumentsResponse> approveDocument(@PathVariable("driverId") int driverId) {
        DriverDocumentsResponse response = validatorService.approveDocument(driverId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/documents/reject/{driverId}")
    public ResponseEntity<DriverDocumentsResponse> rejectDocument(
            @PathVariable("driverId") int driverId,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "Document verification failed");
        DriverDocumentsResponse response = validatorService.rejectDocument(driverId, reason);
        return ResponseEntity.ok(response);
    }
}
