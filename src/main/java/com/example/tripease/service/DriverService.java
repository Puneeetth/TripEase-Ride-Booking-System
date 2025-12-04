package com.example.tripease.service;

import com.example.tripease.dto.request.DriverRequest;
import com.example.tripease.dto.response.DriverResponse;
import com.example.tripease.exception.DriverNotFoundException;
import com.example.tripease.model.Driver;
import com.example.tripease.repository.DriverRepository;
import com.example.tripease.transformer.CustomerTransformer;
import com.example.tripease.transformer.DriverTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    DriverRepository driverRepository;
    public DriverResponse addDriver(DriverRequest driverRequest) {
       Driver driver = DriverTransformer.driverRequestToDriver(driverRequest);
      Driver savedDriver = driverRepository.save(driver);
      return DriverTransformer.driverToDriverResponse(savedDriver);

    }

    public DriverResponse getDriver(int driverId) {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if(optionalDriver.isEmpty()){
            throw new DriverNotFoundException("Invalid Driver Id");
        }
        Driver savedDriver = optionalDriver.get();

        return DriverTransformer.driverToDriverResponse(savedDriver);
    }
}
