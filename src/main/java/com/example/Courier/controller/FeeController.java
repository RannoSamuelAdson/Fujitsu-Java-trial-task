package com.example.Courier.controller;


import com.example.Courier.CourierApplication;
import com.example.Courier.model.WeatherInput;
import com.example.Courier.repository.WeatherRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.example.Courier.CourierApplication.repo;

@RestController
public class FeeController {
    @GetMapping("/api/data")
    public String getFeeRequestResponse(@RequestParam String location, String vehicle){
        double fee = getDeliveryFee(location, vehicle);
        if (fee == -1.0 && !location.equals(""))//if no such station in database and city value has been input
            return "There was an issue with loading weather data. Check your internet connection.";
        else if (fee == -2) return "Usage of selected vehicle type is forbidden";
        else if (fee < -2) return "Enter city name and vehicle before submitting.";
        return "The fee for this delivery is " + fee + "€.";
    }

    public double getDeliveryFee(String location, String vehicle){

        WeatherInput station = getStationData(location);
        if (Objects.equals(station.getStation_name(), "No Such station"))
            return -1.0;//If this station wasn't in the database

        double fee = calculateRegionalBaseFee(location,vehicle);
        double extraFees = calculateExtraFees(station,vehicle);
        if (extraFees == -1)
            return -2.0;//if weather is hazardous for this vehicle

        fee += extraFees;
        return fee;
    }
    public double calculateExtraFees(WeatherInput station, String vehicle){
        double extraFees = 0.0;

        int weatherSeverity = determineWeatherSeverity(station.getPhenomenon());

        if (vehicle.equals("Scooter") || vehicle.equals("Bike")){

            //Checking for ait temperature
            if (station.getAir_temp() <= 0 && station.getAir_temp() >= -10)
                extraFees += 0.5;

            else if (station.getAir_temp() < -10)
                extraFees += 1.0;


            //Checking for weather phenomenons, such as rain or snow
            if (weatherSeverity == 1); //since having no difficult weather phenomenon is the norm in Estonia,
                // It would be wasteful to check for all other situations each time this is the case

            else if (weatherSeverity == 2)//raining
                extraFees += 0.5;
            else if (weatherSeverity == 3)//snow or sleet
                extraFees += 1.0;
            else //hazardous weather conditions
                return -1;//Send out a negative value, that "getFeeRequestResponse()" function would notice it


        }
        if (vehicle.equals("Bike")){
            double windspeed = station.getWindSpeed();

            if (windspeed >= 10 && windspeed <= 20) //greater wins speeds
                return extraFees + 0.5;//return, because

            else if (windspeed > 20) {//hazardous weather conditions
                return -1;//Send out a negative value, that "getFeeRequestResponse()" function would notice it
            }

        }

        return extraFees;
    }

    public int determineWeatherSeverity(String phenomenon){
        //returns:
        //In case the weather phenomenon is glaze, hail, thunder, or thunderstorm returns 4
        //If weather phenomenon is related to snow or sleet, returns 3
        //If weather phenomenon is related to rain, returns 2
        //If weather is none of the above, returns 1
        if (phenomenon.equals("Glaze") || phenomenon.equals("Hail") || phenomenon.equals("Thunder") || phenomenon.equals("Thunderstorm"))
            return 4;
        if (phenomenon.contains("snow") || phenomenon.contains("sleet"))
            return 3; //all possible values related to snow or sleet have these words in them
        if (phenomenon.contains("rain")||phenomenon.contains("shower"))
            return 2; //all possible values related to rain have these words in them

        return 1;//if none of the above

    }
    public double calculateRegionalBaseFee(String location, String vehicle){

        if (location.equals("Tallinn-Harku")) {
            if (vehicle.equals("Car")) return 4.0;
            if (vehicle.equals("Scooter")) return 3.5;
            if (vehicle.equals("Bike")) return 3.0;
        }
        else if (location.equals("Tartu-Tõravere")){
            if (vehicle.equals("Car")) return 3.5;
            if (vehicle.equals("Scooter")) return 3.0;
            if (vehicle.equals("Bike")) return 2.5;
        }
        else if (location.equals("Pärnu")){
            if (vehicle.equals("Car")) return 3.0;
            if (vehicle.equals("Scooter")) return 2.5;
            if (vehicle.equals("Bike")) return 2.0;
        }
        return -200.0;//if none of the options apply
    }

    public static WeatherInput getStationData(String stationName){
        return repo.findById(stationName)
                .orElse(new WeatherInput("No Such station",null,null,null,null,null));//using such a string as a station name so, that I could find out if station exists later on.
    }
}
