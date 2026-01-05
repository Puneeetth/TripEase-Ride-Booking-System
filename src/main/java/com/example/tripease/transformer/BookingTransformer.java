package com.example.tripease.transformer;

import com.example.tripease.Enum.TripStatus;
import com.example.tripease.dto.request.BookingRequest;
import com.example.tripease.dto.response.BookingResponse;
import com.example.tripease.model.Booking;
import com.example.tripease.model.Cab;
import com.example.tripease.model.Customer;
import com.example.tripease.model.Driver;

public class BookingTransformer {
    public static Booking bookingRequestToBooking(BookingRequest bookingRequest, double perKmRate) {
        return Booking.builder()
                .pickupAddress(bookingRequest.getPickup())
                .destinationAddress(bookingRequest.getDestination())
                .tripDistanceInKm(bookingRequest.getTripDistanceInKm())
                .tripStatus(TripStatus.PENDING)
                .billAmount(bookingRequest.getTripDistanceInKm() * perKmRate)
                .build();
    }

    public static BookingResponse bookingToBookingResponse(Booking booking,
            Customer customer,
            Cab cab,
            Driver driver) {
        return BookingResponse.builder()
                .pickup(booking.getPickupAddress())
                .destination(booking.getDestinationAddress())
                .tripDistanceInKm(booking.getTripDistanceInKm())
                .tripStatus(booking.getTripStatus())
                .billAmount(booking.getBillAmount())
                .bookedAt(booking.getBookedAt())
                .lastUpdatedAt(booking.getLastUpdatedAt())
                .customer(CustomerTransformer.customerToCustomerResponse(customer))
                .cab(CabTransformer.cabToCabResponse(cab, driver))
                .build();
    }
}
