package com.example.Courier.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class WeatherInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generated primary key
    private Integer id; // Primary key

    private String stationName;
    private Integer WMO;
    private Float airTemp;
    private Float windSpeed;
    private String phenomenon;
    private Timestamp timestamp;



    public String getStation_name() {
        return stationName;
    } // Standard getter.


    public Float getAir_temp() {
        return airTemp;
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

    public WeatherInput(String stationName, Integer WMO, Float airTemp, Float windSpeed, String phenomenon, Timestamp timestamp) { // Standard constructor.
        this.stationName = stationName;
        this.WMO = WMO;
        this.airTemp = airTemp;
        this.windSpeed = windSpeed;
        this.phenomenon = phenomenon;
        this.timestamp = timestamp;
    }

}