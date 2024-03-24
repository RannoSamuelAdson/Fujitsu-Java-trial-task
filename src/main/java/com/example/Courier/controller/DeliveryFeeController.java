package com.example.Courier.controller;



import com.example.Courier.model.WeatherInput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import java.util.Objects;

import static com.example.Courier.CourierApplication.repository;

@RestController
public class DeliveryFeeController {

    @Autowired
    public Environment environment;

    public DeliveryFeeController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/api/data")
    public String getFeeRequestResponse(@RequestParam String location, String vehicle){
        double fee = getDeliveryFee(location, vehicle);
        if (fee == -1.0 && !location.equals(""))// If no such station in database and city value has been input.
            return "There was an issue with loading weather data. Try again later.";
        if (fee == -2) return "Usage of selected vehicle type is forbidden";
        if (fee < -2) return "Enter city name and vehicle before submitting.";
        return "The fee for this delivery is " + fee + "€.";
    }

    public double getDeliveryFee(String location, String vehicle){

        WeatherInput station = getStationData(location);
        if (Objects.equals(station, null) && !Objects.equals(location, ""))
            return -1.0;// If this station wasn't in the database.

        double fee = calculateRegionalBaseFee(location,vehicle);
        double extraFees = 0;
        if (fee != -200)
            extraFees = calculateExtraFees(station,vehicle);
        if (extraFees == -1)
            return -2.0;// If weather is hazardous for this vehicle.

        fee += extraFees;
        return fee;// If XML webpage malfunction(then about -200) or standard output.
    }
    public double calculateExtraFees(WeatherInput station, String vehicle){
        double extraFees = 0.0; // Starts adding to it, depending on conditions.

        int weatherSeverity = determineWeatherSeverity(station.getPhenomenon());

        if (vehicle.equals("Scooter") || vehicle.equals("Bike")){

            // Checking for ait temperature.
            if (station.getAirTemperature() <= 0 && station.getAirTemperature() >= -10)
                extraFees += 0.5;

            if (station.getAirTemperature() < -10)
                extraFees += 1.0;


            //Checking for weather phenomenons, such as rain or snow.

            if (weatherSeverity == 1); // Since having no difficult weather phenomenon is the norm in Estonia,
                // it would be wasteful to check for all other situations each time this is the case.

            else if (weatherSeverity == 2)// If raining,
                extraFees += 0.5;
            else if (weatherSeverity == 3)// If snow or sleet.
                extraFees += 1.0;
            else // If hazardous weather conditions.
                return -1;// Send out a negative value, that "getFeeRequestResponse()" function would notice it.


        }
        if (vehicle.equals("Bike")){
            double windspeed = station.getWindSpeed();

            if (windspeed >= 10 && windspeed <= 20)
                return extraFees + 0.5;

            if (windspeed > 20) {// Hazardous weather conditions.
                return -1;// Send out a negative value, that "getFeeRequestResponse()" function would notice it.
            }

        }

        return extraFees;
    }

    public int determineWeatherSeverity(String phenomenon){
        // Returns numbers 4-1. The larger the number, the more hazardous the weather.
        // The hazard level is classified by the extra fee phenomenon requirements.
        if (phenomenon.equals("Glaze") || phenomenon.equals("Hail") || phenomenon.equals("Thunder") || phenomenon.equals("Thunderstorm"))
            return 4;
        if (phenomenon.contains("snow") || phenomenon.contains("sleet"))
            return 3; //all possible values related to snow or sleet have these words in them
        if (phenomenon.contains("rain")||phenomenon.contains("shower"))
            return 2; //all possible values related to rain have these words in them

        return 1;//if none of the above

    }
    public double calculateRegionalBaseFee(String location, String vehicle) {
        String propertyName = String.format("location.fees.%s.%s", location, vehicle);
        String feeValue = environment.getProperty(propertyName);

        if (feeValue != null) {
            return Double.parseDouble(feeValue);
        } else {
            return -200.0; // If none of the options apply
        }
    }

    private static WeatherInput getStationData(String location) {
        long repositorySize = repository.count();



        for (long i = 0; i < 3; i++) {
            Integer weatherInputIndex = Math.toIntExact(repositorySize - i);
            WeatherInput weatherInput = repository.findById(weatherInputIndex).orElse(null);
            if (weatherInput != null && (Objects.equals(weatherInput.getStationName(), location)))
                return weatherInput;
        }
        return null;
    }

}
