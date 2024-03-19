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
    private String station_name;
    private Integer WMO;
    private Float air_temp;
    private Float wind_speed;
    private String phenomenon;
    private Timestamp time_stamp;


    public static boolean doesStationExist(WeatherInput station){
        return (!Objects.equals(station.getStation_name(), "No Such station"));
    }
    public String getStation_name() {
        return station_name;
    } //getters for needed values


    public Float getAir_temp() {
        return air_temp;
    }

    public Float getWindSpeed() {
        return wind_speed;
    }

    public String getPhenomenon() {
        return phenomenon;
    }


    public WeatherInput() {
        // Default constructor required by Hibernate
    }

    public WeatherInput(String station_name, Integer WMO, Float air_temp, Float wind_speed, String phenomenon, Timestamp time_stamp) {
        this.station_name = station_name;
        this.WMO = WMO;
        this.air_temp = air_temp;
        this.wind_speed = wind_speed;
        this.phenomenon = phenomenon;
        this.time_stamp = time_stamp;
    }


}