package com.example.tripease.dto.response;

import com.example.tripease.Enum.TripStatus;
import lombok.*;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookingResponse {
    String pickup;
    String destination;
    double tripDistanceInKm;
    TripStatus tripStatus;
    double billAmount;

    Date bookedAt;

    Date lastUpdatedAt;

    CustomerResponse customer;

    CabResponse cab;
}
