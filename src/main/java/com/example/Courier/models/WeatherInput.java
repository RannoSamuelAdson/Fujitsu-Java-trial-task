package com.example.Courier.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity
public class WeatherInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generated primary key
    private Integer id; // Primary key

    private String stationName;
    private Integer WMO;
    private Float airTemperature;
    private Float windSpeed;
    private String phenomenon;
    private Timestamp timestamp;



    public String getStationName() {
        return stationName;
    } // Standard getter.


    public Float getAirTemperature() {
        return airTemperature;
    } // Standard getter.

    public Float getWindSpeed() {
        return windSpeed;
    } // Standard getter.

    public String getPhenomenon() {
        return phenomenon;
    } // Standard getter.


    public WeatherInput() {
        // Default constructor required by Hibernate.
    }

    public WeatherInput(String stationName, Integer WMO, Float airTemperature, Float windSpeed, String phenomenon, Timestamp timestamp) { // Standard constructor.
        this.stationName = stationName;
        this.WMO = WMO;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.phenomenon = phenomenon;
        this.timestamp = timestamp;
    }

}