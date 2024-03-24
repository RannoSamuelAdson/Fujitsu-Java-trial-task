package com.example.Courier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.weather")
public class WeatherConfig {

    private List<String> stations;

    public List<String> getStations() {
        return stations;
    }


    public void setStations(List<String> stations) {
        this.stations = stations;
    }
}