package com.example.tripease.controller;

import com.example.tripease.dto.request.CustomerRegisterRequest;
import com.example.tripease.dto.request.DriverRegisterRequest;
import com.example.tripease.dto.request.LoginRequest;
import com.example.tripease.dto.response.AuthResponse;
import com.example.tripease.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/driver")
    public ResponseEntity<AuthResponse> registerDriver(@Valid @RequestBody DriverRegisterRequest request) {
        return ResponseEntity.ok(authService.registerDriver(request));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponse> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.ok(authService.registerCustomer(request));
    }

    @PostMapping("/login/driver")
    public ResponseEntity<AuthResponse> loginDriver(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginDriver(request));
    }

    @PostMapping("/login/customer")
    public ResponseEntity<AuthResponse> loginCustomer(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginCustomer(request));
    }

    @PostMapping("/login/validator")
    public ResponseEntity<AuthResponse> loginValidator(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginValidator(request));
    }

    @PostMapping("/register/validator")
    public ResponseEntity<AuthResponse> registerValidator(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.registerValidator(request));
    }
}
