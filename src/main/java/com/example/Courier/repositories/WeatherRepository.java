package com.example.Courier.repositories;

import com.example.Courier.models.WeatherInput;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends CrudRepository<WeatherInput,Integer> { //defines the repository
}
