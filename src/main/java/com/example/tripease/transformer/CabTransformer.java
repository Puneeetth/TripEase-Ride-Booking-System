package com.example.tripease.transformer;

import com.example.tripease.dto.request.CabRequest;
import com.example.tripease.dto.response.CabResponse;
import com.example.tripease.model.Cab;
import com.example.tripease.model.Driver;

public class CabTransformer {
    public static Cab cabRequestToCab(CabRequest cabRequest){
        return Cab.builder()
                .cabNumber(cabRequest.getCabNumber())
                .cabModel(cabRequest.getCabModel())
                .perKmRate(cabRequest.getPerKmRate())
                .available(true)
                .build();
    }
    public static CabResponse cabToCabResponse(Cab cab, Driver driver){
        return CabResponse.builder()
                .cabNumber(cab.getCabNumber())
                .perKmRate(cab.getPerKmRate())
                .cabModel(cab.getCabModel())
                .availabe(cab.isAvailable())
                .driver(DriverTransformer.driverToDriverResponse(driver))
                .build();
    }
}
