package com.example.tripease.controller;

import com.example.tripease.dto.request.FareCalculationRequest;
import com.example.tripease.dto.response.FareCalculationResponse;
import com.example.tripease.service.FareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fare")
@RequiredArgsConstructor
public class FareController {

    private final FareService fareService;

    @PostMapping("/calculate")
    public ResponseEntity<FareCalculationResponse> calculateFare(@RequestBody FareCalculationRequest request) {
        FareCalculationResponse response = fareService.calculateFare(request);

        if (response.getFareEstimates() == null || response.getFareEstimates().isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
