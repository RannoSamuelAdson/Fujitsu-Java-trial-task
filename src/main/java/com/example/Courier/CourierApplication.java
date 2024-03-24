package com.example.Courier;


import com.example.Courier.repository.WeatherRepository;
import com.example.Courier.service.CronJobs.WeatherInformationFetcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourierApplication {
	public static WeatherRepository repository;
	public static void main(String[] args) {//Main function, run it to activate the application
		ConfigurableApplicationContext context = SpringApplication.run(CourierApplication.class, args);
		repository = context.getBean(WeatherRepository.class);//get a database to work with
		WeatherInformationFetcher weatherInformationFetcher = new WeatherInformationFetcher(repository);
		weatherInformationFetcher.updateDatabase();//insert values into database

	}

}
