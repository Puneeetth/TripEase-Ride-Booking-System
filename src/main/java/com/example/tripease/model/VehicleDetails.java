package com.example.tripease.model;

import com.example.tripease.Enum.VehicleType;
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
@Table(name = "vehicle_details")
public class VehicleDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$", message = "Invalid Registration Number")
    private String registrationNumber;

    @Column(unique = true, nullable = false)
    private String insuranceNumber;

    @Column(nullable = false)
    private LocalDate insuranceExpiryDate;

    @Column(unique = true, nullable = false)
    private String rcNumber;

    @OneToOne
    @JoinColumn(name = "driver_id", unique = true)
    private Driver driver;
}
