package com.example.Courier;


import com.example.Courier.model.WeatherInput;
import com.example.Courier.repository.WeatherRepo;
import com.example.Courier.service.CronJobService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling
public class CourierApplication {
	public static WeatherRepo repo;
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(CourierApplication.class, args);
		repo = context.getBean(WeatherRepo.class);//get a database to work with
		CronJobService.updateDatabase(repo);//insert values into database


	}





}

