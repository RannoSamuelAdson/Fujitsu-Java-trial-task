package com.example.Courier.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getFeeRequestResponse(@RequestParam String location, String vehicle){
        double fee = deliveryFeeController.getDeliveryFee(location, vehicle);
        if (fee == -1.0 && !location.equals(""))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("There was an issue with loading weather data. Try again later.");

        if (fee == -2)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usage of selected vehicle type is forbidden");

        if (fee < -2)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Enter city name and vehicle before submitting.");

        return ResponseEntity.ok("The fee for this delivery is " + fee + "€.");
    }
}
