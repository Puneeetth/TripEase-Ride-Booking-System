package com.example.tripease.service;

import com.example.tripease.Enum.DocumentStatus;
import com.example.tripease.Enum.Role;
import com.example.tripease.dto.request.CustomerRegisterRequest;
import com.example.tripease.dto.request.DriverRegisterRequest;
import com.example.tripease.dto.request.LoginRequest;
import com.example.tripease.dto.response.AuthResponse;
import com.example.tripease.exception.BadRequestException;
import com.example.tripease.exception.DuplicateResourceException;
import com.example.tripease.exception.ResourceNotFoundException;
import com.example.tripease.exception.UnauthorizedException;
import com.example.tripease.model.Customer;
import com.example.tripease.model.Driver;
import com.example.tripease.model.User;
import com.example.tripease.repository.CustomerRepository;
import com.example.tripease.repository.DriverDocumentsRepository;
import com.example.tripease.repository.DriverRepository;
import com.example.tripease.repository.UserRepository;
import com.example.tripease.security.JwtService;
import com.example.tripease.transformer.AuthResponseTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final DriverRepository driverRepository;
        private final DriverDocumentsRepository driverDocumentsRepository;
        private final CustomerRepository customerRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Transactional
        public AuthResponse registerDriver(DriverRegisterRequest request) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmailId())) {
                        throw new DuplicateResourceException("Email already registered");
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

                return AuthResponseTransformer.toDriverRegistrationSuccess(user, savedDriver, token);
        }

        @Transactional
        public AuthResponse registerCustomer(CustomerRegisterRequest request) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmailId())) {
                        throw new DuplicateResourceException("Email already registered");
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

                return AuthResponseTransformer.toCustomerRegistrationSuccess(user, savedCustomer.getCustomerId(),
                                token);
        }

        public AuthResponse loginDriver(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        throw new UnauthorizedException("Invalid email or password");
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Validate that user is a DRIVER
                if (user.getRole() != Role.DRIVER) {
                        throw new UnauthorizedException("This login is for drivers only. Please use customer login.");
                }

                // Get driver
                Driver driver = driverRepository.findById(user.getReferenceId())
                                .orElse(null);
                String driverName = driver != null ? driver.getName() : "";

                // Check if documents are approved
                var documentsOpt = driverDocumentsRepository.findByDriverDriverId(user.getReferenceId());
                if (documentsOpt.isEmpty()) {
                        throw new BadRequestException("Please complete document verification first");
                }

                var documents = documentsOpt.get();
                if (documents.getDocumentStatus() != DocumentStatus.APPROVED) {
                        String statusMessage = documents.getDocumentStatus() == DocumentStatus.PENDING
                                        ? "Your documents are pending verification. Please wait for approval."
                                        : "Your documents were rejected. Reason: " + documents.getRejectedReason();
                        throw new BadRequestException(statusMessage);
                }

                String token = jwtService.generateToken(user);

                return AuthResponseTransformer.toDriverLoginSuccess(user, driverName, token);
        }

        public AuthResponse loginCustomer(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        throw new UnauthorizedException("Invalid email or password");
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Validate that user is a CUSTOMER
                if (user.getRole() != Role.CUSTOMER) {
                        throw new UnauthorizedException("This login is for customers only. Please use driver login.");
                }

                String token = jwtService.generateToken(user);

                return AuthResponseTransformer.toCustomerLoginSuccess(user, token);
        }

        public AuthResponse loginValidator(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        throw new UnauthorizedException("Invalid email or password");
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Validate that user is a VALIDATOR
                if (user.getRole() != Role.VALIDATOR) {
                        throw new UnauthorizedException("Access denied. This login is for validators only.");
                }

                String token = jwtService.generateToken(user);

                return AuthResponseTransformer.toValidatorLoginSuccess(user, token);
        }

        @Transactional
        public AuthResponse registerValidator(LoginRequest request) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new DuplicateResourceException("Email already registered");
                }

                // Create User entity for validator
                User user = User.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.VALIDATOR)
                                .referenceId(0)
                                .build();
                userRepository.save(user);

                return AuthResponseTransformer.toValidatorRegistrationSuccess();
        }
}
