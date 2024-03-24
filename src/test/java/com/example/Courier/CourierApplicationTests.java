package com.example.Courier;





import com.example.Courier.controllers.DeliveryFeeController;
import com.example.Courier.controllers.HTTPRequestController;
import com.example.Courier.models.WeatherInput;
import com.example.Courier.repositories.WeatherRepository;
import com.example.Courier.service.CronJobs.WeatherInformationFetcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.Courier.CourierApplication.repository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
	class HTTPRequestControllerTest{
	private HTTPRequestController httpRequestController;

	@Mock
	private WeatherRepository weatherRepositoryMock;

	@Mock
	private Environment environment;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		CourierApplication.repository = weatherRepositoryMock;
		DeliveryFeeController deliveryFeeController = new DeliveryFeeController(environment);
		httpRequestController = new HTTPRequestController(deliveryFeeController);
	}
	//getFeeRequestResponse(String location, String vehicle)
	/*
	1. (location = "Pärnu", vehicle = Car, database doesn't have Pärnu): return "There was an issue with loading weather data. Check your internet connection."
	2. (location = "Pärnu", vehicle = Bike, windspeed = 25): return "Usage of selected vehicle type is forbidden"
	3. (location = "", vehicle = ""): return "Enter city name and vehicle before submitting."
	4. (location = "Tallinn-Harku", vehicle = Car): return "The fee for this delivery is 4€."
	*/
	@Test
	void testgetFeeRequestResponse_Pärnu_Car_CityNotFound() {
		// Arrange
		// Ensuring that fetching of elements returns correctly.
		when(repository.count()).thenReturn(0L);

		// Act
		ResponseEntity<String> responseEntity = httpRequestController.getFeeRequestResponse("Pärnu", "Car");

		// Assert
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
		assertEquals("There was an issue with loading weather data. Try again later.", responseEntity.getBody());
	}

	@Test
	void testgetFeeRequestResponse_Pärnu_Bike_WindSpeed25() {
		// Arrange
		WeatherInput station = new WeatherInput("Pärnu",41803,-5.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));
		// Ensuring that fetching of elements returns correctly.
		when(repository.count()).thenReturn(3L);
		when(repository.findById(3)).thenReturn(Optional.of(station));
		when(environment.getProperty("location.fees.Pärnu.Bike")).thenReturn("2.0");

		// Act
		ResponseEntity<String> responseEntity = httpRequestController.getFeeRequestResponse("Pärnu","Bike");

		// Assert
		assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
		assertEquals("Usage of selected vehicle type is forbidden",responseEntity.getBody());
	}

	@Test
	void testgetFeeRequestResponse_EmptyLocationAndVehicle() {

		// Act
		ResponseEntity<String> responseEntity =  httpRequestController.getFeeRequestResponse("","");

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals("Enter city name and vehicle before submitting.",responseEntity.getBody());
	}

	@Test
	void testgetFeeRequestResponse_Tallinn_Car() {
		// Arrange
		WeatherInput station = new WeatherInput("Tallinn-Harku",41803,-5.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));

		// Ensuring that fetching of elements returns correctly.
		when(repository.count()).thenReturn(3L);
		when(repository.findById(3)).thenReturn(Optional.of(station));
		when(environment.getProperty("location.fees.Tallinn-Harku.Car")).thenReturn("4.0");

		// Act
		ResponseEntity<String> responseEntity = httpRequestController.getFeeRequestResponse("Tallinn-Harku", "Car");

		// Assert
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("The fee for this delivery is 4.0€.", responseEntity.getBody());
	}
}

@SpringBootTest
class DeliveryFeeControllerTest {

	@Autowired
	private DeliveryFeeController deliveryFeeController;

	@Mock
	private WeatherRepository weatherRepositoryMock;

	@Mock
	private Environment environment;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		CourierApplication.repository = weatherRepositoryMock;
		deliveryFeeController = new DeliveryFeeController(environment);
	}

	//CalculateRegionalBaseFee(String location, String vehicle) tests
	/*
	1. (location = Tallinn-Harku , vehicle = Car): return 4
	2. (location = "", vehicle = ""): return -200
	* */
	@Test
	void testCalculateRegionalBaseFee_fee_exists () {
		when(environment.getProperty("location.fees.Tallinn-Harku.Car")).thenReturn("4.0");
	assertEquals(4.0, deliveryFeeController.calculateRegionalBaseFee("Tallinn-Harku", "Car"));
	}

	@Test
	void testCalculateRegionalBaseFee_no_fee_exists () {
	assertEquals(-200.0, deliveryFeeController.calculateRegionalBaseFee("", ""));
	}


		/*
	//determineWeatherSeverity(String phenomenon)
	1. (phenomenon = Hail): return 4
	2. (phenomenon = Moderate snow shower): return 3
	3. (phenomenon = Light rain): return 2
	4. (phenomenon = ""): return 1
	 */
	@Test
	void testdetermineWeatherSeverity_Hail() {
		assertEquals(4, deliveryFeeController.determineWeatherSeverity("Hail"));
	}

	@Test
	void testdetermineWeatherSeverity_Moderate_snow_shower() {
		assertEquals(3, deliveryFeeController.determineWeatherSeverity("Moderate snow shower"));
	}
	@Test
	void testdetermineWeatherSeverity_Light_rain() {
		assertEquals(2, deliveryFeeController.determineWeatherSeverity("Light rain"));
	}
	@Test
	void testdetermineWeatherSeverity_EmptyString() {
		assertEquals(1, deliveryFeeController.determineWeatherSeverity(""));
	}
//calculateExtraFees(WeatherInput station, String vehicle)
	/*
	1.(vehicle = Bike, station is new Weatherinput(where temp is -5, weatherSeverity is 1, windspeed 15): return 1
	2.(vehicle = Bike, station is new Weatherinput(where temp is -20, weatherSeverity is 2, windspeed 25): return -1
	3.(vehicle = Bike, station is new Weatherinput(where temp is 5, weatherSeverity is 3, windspeed 3): return 0.5
	4.(vehicle = Scooter, station is new Weatherinput(where weatherSeverity is 4): return -1
	5.(vehicle = car, station is a random WeatherInput): return 0
	*/

	@Test
	void testcalculateExtraFees_Bike_tempMinus5_phenomenonClear_WindSpeed15() {
		WeatherInput station = new WeatherInput("Pärnu",41803,-5.0f,15.0f,"Clear",new Timestamp(System.currentTimeMillis()));

		assertEquals(1, deliveryFeeController.calculateExtraFees(station,"Bike"));
	}
	@Test
	void testcalculateExtraFees_Bike_tempMinus20_phenomenonSnow_WindSpeed25() {
		WeatherInput station = new WeatherInput("Pärnu",41803,-20.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));

		assertEquals(-1, deliveryFeeController.calculateExtraFees(station,"Bike"));
	}
	@Test
	void testcalculateExtraFees_Bike_temp5_phenomenonRain_WindSpeed3() {
		WeatherInput station = new WeatherInput("Pärnu",41803,5.0f,3.0f,"Light rain",new Timestamp(System.currentTimeMillis()));

		assertEquals(0.5f, deliveryFeeController.calculateExtraFees(station,"Bike"));
	}
	@Test
	void testcalculateExtraFees_Scooter_phenomenonHail(){
		WeatherInput station = new WeatherInput("Pärnu",41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		assertEquals(-1, deliveryFeeController.calculateExtraFees(station,"Scooter"));
	}
	/*@Test
	void testcalculateExtraFees_Car(){
		WeatherInput station = new WeatherInput("Pärnu",41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		assertEquals(0, controller.calculateExtraFees(station,"Car"));
	}




	//getDeliveryFee(String location, String vehicle)
	/*
	1. (location = "Pärnu", database does not have Pärnu, vehcle = Scooter): return -1
	2. (location = "Pärnu", vehicle = Bike, weatherPhenomenon = Hail): return -2
	3. (location = "Pärnu", vehicle = car): return 3
	 */
	@Test
	void testgetDeliveryFee_Pärnu_Scooter_CityNotFound() {
		// Arrange
		// Ensuring that fetching of elements returns correctly.
		when(repository.count()).thenReturn(3L);
		when(repository.findById(3)).thenReturn(Optional.empty());


		// Act
		double fee = deliveryFeeController.getDeliveryFee("Pärnu","Scooter");

		// Assert
		assertEquals(-1, fee);
	}

	@Test
	void testgetDeliveryFee_Pärnu_Bike_phenomenonHail() {
		// Arrange
		WeatherInput weatherInput = new WeatherInput("Pärnu", 41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));

		// Ensuring that fetching of elements returns correctly.
		when(repository.count()).thenReturn(3L);
		when(repository.findById(3)).thenReturn(Optional.of(weatherInput));
		when(environment.getProperty("location.fees.Pärnu.Bike")).thenReturn("2.0");

		// Act
		double fee = deliveryFeeController.getDeliveryFee("Pärnu","Bike");

		// Assert
		assertEquals(-2, fee);
	}

	@Test
	void testgetDeliveryFee_Pärnu_Car() {
		// Arrange
		WeatherInput weatherInput = new WeatherInput("Pärnu", 41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		// Ensuring that fetching of elements returns correctly.
		when(repository.count()).thenReturn(3L);
		when(repository.findById(3)).thenReturn(Optional.of(weatherInput));
		when(environment.getProperty("location.fees.Pärnu.Car")).thenReturn("3.0");

		// Act
		double fee = deliveryFeeController.getDeliveryFee("Pärnu","Car");

		// Assert
		assertEquals(3, fee);
	}

	}
@SpringBootTest
class CronJobServiceTest {

	@Mock
	private WeatherRepository weatherRepositoryMock;

	@Test
	void testupdateDatabase_validURL() throws Exception {
		//Tests that if updateDatabase has been called with a valid URL, then:
		//1. Old weather records were wiped(avoids data duplication and storing of unnecessary data).
		//2. 3 WeatherInput objects were saved into the database.
		//3. at least one of these objects had the station name of "Tallinn-Harku".

		// Call the method to be tested
		WeatherInformationFetcher fetcher = new WeatherInformationFetcher(weatherRepositoryMock);
		fetcher.updateDatabase();

		ArgumentCaptor<WeatherInput> captor = ArgumentCaptor.forClass(WeatherInput.class);
		verify(weatherRepositoryMock, times(3)).save(captor.capture());

		// Get the captured WeatherInput objects
		List<WeatherInput> capturedWeatherInputs = captor.getAllValues();

		// Assert that one of the objects saved was by the name of "Tallinn-Harku"
		assertTrue(capturedWeatherInputs.stream()
				.anyMatch(input -> Objects.equals(input.getStationName(), "Tallinn-Harku")));

	}



}