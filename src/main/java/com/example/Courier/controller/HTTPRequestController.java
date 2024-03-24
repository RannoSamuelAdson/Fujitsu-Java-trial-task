package com.example.Courier.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HTTPRequestController {

    private DeliveryFeeController deliveryFeeController;

    public HTTPRequestController(DeliveryFeeController deliveryFeeController) {
        this.deliveryFeeController = deliveryFeeController;
    }

    @GetMapping("/api/data")
    public String getFeeRequestResponse(@RequestParam String location, String vehicle){
        double fee = deliveryFeeController.getDeliveryFee(location, vehicle);
        if (fee == -1.0 && !location.equals(""))// If no such station in database and city value has been input.
            return "There was an issue with loading weather data. Try again later.";
        if (fee == -2) return "Usage of selected vehicle type is forbidden";
        if (fee < -2) return "Enter city name and vehicle before submitting.";
        return "The fee for this delivery is " + fee + "€.";
}
}
