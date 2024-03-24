package com.example.Courier.repository;

import com.example.Courier.model.WeatherInput;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends CrudRepository<WeatherInput,Integer> { //defines the repository
}
