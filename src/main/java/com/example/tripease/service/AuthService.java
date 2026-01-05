package com.example.tripease.service;

import com.example.tripease.Enum.Role;
import com.example.tripease.dto.request.CustomerRegisterRequest;
import com.example.tripease.dto.request.DriverRegisterRequest;
import com.example.tripease.dto.request.LoginRequest;
import com.example.tripease.dto.response.AuthResponse;
import com.example.tripease.model.Customer;
import com.example.tripease.model.Driver;
import com.example.tripease.model.User;
import com.example.tripease.repository.CustomerRepository;
import com.example.tripease.repository.DriverRepository;
import com.example.tripease.repository.UserRepository;
import com.example.tripease.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final DriverRepository driverRepository;
        private final CustomerRepository customerRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Transactional
        public AuthResponse registerDriver(DriverRegisterRequest request) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmailId())) {
                        return AuthResponse.builder()
                                        .message("Email already registered")
                                        .build();
                }

                // Create Driver entity
                Driver driver = Driver.builder()
                                .name(request.getName())
                                .age(request.getAge())
                                .emailId(request.getEmailId())
                                .build();
                Driver savedDriver = driverRepository.save(driver);

                // Create User entity for authentication
                User user = User.builder()
                                .email(request.getEmailId())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.DRIVER)
                                .referenceId(savedDriver.getDriverId())
                                .build();
                userRepository.save(user);

                // Generate JWT token
                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .email(user.getEmail())
                                .name(savedDriver.getName())
                                .role(user.getRole())
                                .referenceId(savedDriver.getDriverId())
                                .message("Driver registered successfully")
                                .build();
        }

        @Transactional
        public AuthResponse registerCustomer(CustomerRegisterRequest request) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmailId())) {
                        return AuthResponse.builder()
                                        .message("Email already registered")
                                        .build();
                }

                // Create Customer entity
                Customer customer = Customer.builder()
                                .name(request.getName())
                                .age(request.getAge())
                                .gender(request.getGender())
                                .emailId(request.getEmailId())
                                .build();
                Customer savedCustomer = customerRepository.save(customer);

                // Create User entity for authentication
                User user = User.builder()
                                .email(request.getEmailId())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.CUSTOMER)
                                .referenceId(savedCustomer.getCustomerId())
                                .build();
                userRepository.save(user);

                // Generate JWT token
                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .email(user.getEmail())
                                .role(user.getRole())
                                .referenceId(savedCustomer.getCustomerId())
                                .message("Customer registered successfully")
                                .build();
        }

        public AuthResponse loginDriver(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        return AuthResponse.builder()
                                        .message("Invalid email or password")
                                        .build();
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Validate that user is a DRIVER
                if (user.getRole() != Role.DRIVER) {
                        return AuthResponse.builder()
                                        .message("This login is for drivers only. Please use customer login.")
                                        .build();
                }

                // Get driver name
                Driver driver = driverRepository.findById(user.getReferenceId())
                                .orElse(null);
                String driverName = driver != null ? driver.getName() : "";

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .email(user.getEmail())
                                .name(driverName)
                                .role(user.getRole())
                                .referenceId(user.getReferenceId())
                                .message("Driver login successful")
                                .build();
        }

        public AuthResponse loginCustomer(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        return AuthResponse.builder()
                                        .message("Invalid email or password")
                                        .build();
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Validate that user is a CUSTOMER
                if (user.getRole() != Role.CUSTOMER) {
                        return AuthResponse.builder()
                                        .message("This login is for customers only. Please use driver login.")
                                        .build();
                }

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .email(user.getEmail())
                                .role(user.getRole())
                                .referenceId(user.getReferenceId())
                                .message("Customer login successful")
                                .build();
        }
}
