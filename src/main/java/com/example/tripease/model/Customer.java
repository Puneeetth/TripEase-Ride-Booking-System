package com.example.tripease.model;

import com.example.tripease.Enum.Gender;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "customer_table")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;
    private String name;
    private int age;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    @Column(unique = true, nullable = false)
    private String emailId;

    // Removed: Old booking relationship - now handled by booking.customerId
}