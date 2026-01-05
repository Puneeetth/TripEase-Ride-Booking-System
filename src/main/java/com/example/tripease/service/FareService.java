package com.example.tripease.service;

import com.example.tripease.Enum.RideType;
import com.example.tripease.dto.request.FareCalculationRequest;
import com.example.tripease.dto.response.FareCalculationResponse;
import com.example.tripease.dto.response.FareEstimate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FareService {

    // Fare rates for each ride type (INR)
    private static final Map<RideType, FareRates> RIDE_RATES = Map.of(
            RideType.AUTO, new FareRates(25, 12, 1, 30),
            RideType.BIKE, new FareRates(15, 8, 0.5, 20),
            RideType.CAR, new FareRates(50, 15, 2, 80),
            RideType.PREMIUM, new FareRates(100, 25, 3, 150));

    private static final Map<RideType, String> RIDE_ICONS = Map.of(
            RideType.AUTO, "🛺",
            RideType.BIKE, "🏍️",
            RideType.CAR, "🚗",
            RideType.PREMIUM, "🚙");

    private static final Map<RideType, String> RIDE_NAMES = Map.of(
            RideType.AUTO, "Auto",
            RideType.BIKE, "Bike",
            RideType.CAR, "Car",
            RideType.PREMIUM, "Premium");

    public FareCalculationResponse calculateFare(FareCalculationRequest request) {
        // Calculate distance using OSRM (OpenStreetMap Routing)
        DistanceResult distanceResult = getDistanceFromOSRM(
                request.getPickupLat(), request.getPickupLng(),
                request.getDestinationLat(), request.getDestinationLng());

        if (distanceResult == null) {
            return FareCalculationResponse.builder()
                    .message("Could not calculate distance. Please try again.")
                    .build();
        }

        // Calculate fare for all ride types
        List<FareEstimate> fareEstimates = new ArrayList<>();
        for (RideType rideType : RideType.values()) {
            FareEstimate estimate = calculateFareForRideType(rideType, distanceResult);
            fareEstimates.add(estimate);
        }

        return FareCalculationResponse.builder()
                .pickupAddress(request.getPickupAddress())
                .destinationAddress(request.getDestinationAddress())
                .distanceKm(distanceResult.distanceKm)
                .distanceText(distanceResult.distanceText)
                .durationMin(distanceResult.durationMin)
                .durationText(distanceResult.durationText)
                .fareEstimates(fareEstimates)
                .message("Success")
                .build();
    }

    private FareEstimate calculateFareForRideType(RideType rideType, DistanceResult distanceResult) {
        FareRates rates = RIDE_RATES.get(rideType);

        int distanceFare = (int) (distanceResult.distanceKm * rates.perKm);
        int timeFare = (int) (distanceResult.durationMin * rates.perMin);
        int totalFare = rates.baseRate + distanceFare + timeFare;
        int finalFare = Math.max(totalFare, rates.minFare);

        return FareEstimate.builder()
                .rideType(rideType)
                .rideName(RIDE_NAMES.get(rideType))
                .rideIcon(RIDE_ICONS.get(rideType))
                .baseFare(rates.baseRate)
                .distanceFare(distanceFare)
                .timeFare(timeFare)
                .totalFare(finalFare)
                .distanceKm(distanceResult.distanceKm)
                .distanceText(distanceResult.distanceText)
                .durationMin(distanceResult.durationMin)
                .durationText(distanceResult.durationText)
                .build();
    }

    /**
     * Get distance and duration using OSRM (OpenStreetMap Routing Machine)
     * Free, no API key required!
     */
    private DistanceResult getDistanceFromOSRM(double originLat, double originLng,
            double destLat, double destLng) {
        try {
            // OSRM Demo server (for development - use your own server in production)
            String url = String.format(
                    "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                    originLng, originLat, destLng, destLat // Note: OSRM uses lng,lat order
            );

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "Ok".equals(response.get("code"))) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                if (routes != null && !routes.isEmpty()) {
                    Map<String, Object> route = routes.get(0);

                    // Distance in meters, duration in seconds
                    double distanceMeters = ((Number) route.get("distance")).doubleValue();
                    double durationSeconds = ((Number) route.get("duration")).doubleValue();

                    double distanceKm = Math.round(distanceMeters / 100.0) / 10.0; // Round to 1 decimal
                    int durationMin = (int) Math.round(durationSeconds / 60.0);

                    return new DistanceResult(
                            distanceKm,
                            String.format("%.1f km", distanceKm),
                            durationMin,
                            formatDuration(durationMin));
                }
            }
        } catch (Exception e) {
            System.err.println("OSRM API error: " + e.getMessage());
            // Fall back to Haversine calculation
            return calculateHaversineDistance(originLat, originLng, destLat, destLng);
        }

        // Fallback to Haversine
        return calculateHaversineDistance(originLat, originLng, destLat, destLng);
    }

    private String formatDuration(int minutes) {
        if (minutes < 60) {
            return minutes + " mins";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours + " hr " + mins + " mins";
    }

    /**
     * Fallback: Calculate distance using Haversine formula (straight-line * 1.3)
     */
    private DistanceResult calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Multiply by 1.3 to approximate road distance
        double straightDistance = R * c;
        double roadDistance = Math.round(straightDistance * 1.3 * 10.0) / 10.0;
        int durationMin = (int) (roadDistance * 3); // Approximate 3 mins per km

        return new DistanceResult(
                roadDistance,
                String.format("%.1f km", roadDistance),
                durationMin,
                formatDuration(durationMin));
    }

    // Inner classes
    private static class FareRates {
        final int baseRate;
        final double perKm;
        final double perMin;
        final int minFare;

        FareRates(int baseRate, double perKm, double perMin, int minFare) {
            this.baseRate = baseRate;
            this.perKm = perKm;
            this.perMin = perMin;
            this.minFare = minFare;
        }
    }

    private static class DistanceResult {
        final double distanceKm;
        final String distanceText;
        final int durationMin;
        final String durationText;

        DistanceResult(double distanceKm, String distanceText, int durationMin, String durationText) {
            this.distanceKm = distanceKm;
            this.distanceText = distanceText;
            this.durationMin = durationMin;
            this.durationText = durationText;
        }
    }
}
