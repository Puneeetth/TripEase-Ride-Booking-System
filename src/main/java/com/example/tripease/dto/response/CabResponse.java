package com.example.tripease.dto.response;

import com.example.tripease.model.Driver;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CabResponse {
    private String cabNumber;
    private String cabModel;
    private boolean availabe;
    private double perKmRate;
    private DriverResponse driver;
}
