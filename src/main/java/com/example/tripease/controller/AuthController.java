package com.example.tripease.controller;

import com.example.tripease.dto.request.CustomerRegisterRequest;
import com.example.tripease.dto.request.DriverRegisterRequest;
import com.example.tripease.dto.request.LoginRequest;
import com.example.tripease.dto.response.AuthResponse;
import com.example.tripease.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/driver")
    public ResponseEntity<AuthResponse> registerDriver(@RequestBody DriverRegisterRequest request) {
        AuthResponse response = authService.registerDriver(request);
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponse> registerCustomer(@RequestBody CustomerRegisterRequest request) {
        AuthResponse response = authService.registerCustomer(request);
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/driver")
    public ResponseEntity<AuthResponse> loginDriver(@RequestBody LoginRequest request) {
        AuthResponse response = authService.loginDriver(request);
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/customer")
    public ResponseEntity<AuthResponse> loginCustomer(@RequestBody LoginRequest request) {
        AuthResponse response = authService.loginCustomer(request);
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
