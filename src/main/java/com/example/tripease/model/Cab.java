package com.example.tripease.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cabId;
    private String cabNumber;
    private String cabModel;
    @Column(name="available")
    private boolean available;
    private double perKmRate;


}
