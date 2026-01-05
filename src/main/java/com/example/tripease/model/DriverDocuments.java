package com.example.tripease.model;

import com.example.tripease.Enum.DocumentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "driver_documents")
public class DriverDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @Column(unique = true)
    @Pattern(regexp = "[A-Z]{2}[0-9]{13}", message = "Invalid Driving License Number")
    private String driverLicenseNumber;

    private LocalDate expiryDate;

    @Column(unique = true)
    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$", message = "Invalid Aadhaar Number")
    private String aadhaarNumber;

    @Column(unique = true)
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid Pan Number")
    private String panCardNumber;

    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus;

    private String rejectedReason;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
