package com.example.Courier;





import com.example.Courier.controller.FeeController;
import com.example.Courier.model.WeatherInput;
import com.example.Courier.repository.WeatherRepo;
import com.example.Courier.service.CronJobService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
	class FeeControllerTest {

		@Autowired
		private FeeController controller;
		@Mock
		private WeatherRepo weatherRepoMock;

		@BeforeEach
		void setUp() {
			MockitoAnnotations.openMocks(this);
			CourierApplication.repo = weatherRepoMock;
			controller = new FeeController();
		}

	//CalculateRegionalBaseFee(String location, String vehicle) tests
	/*
	1. (location = Tallinn-Harku , vehicle = Car): return 4
	2. (location = Tallinn-Harku, vehicle = Scooter): return 3.5
	3. (location = Tallinn-Harku, vehicle = Bike): return 3
	4. (location = Tartu-Tõravere, vehicle = Car): return 3.5
	5. (location = Tartu-Tõravere, vehicle = Scooter): return 3
	6. (location = Tartu-Tõravere, vehicle = Bike): return 2.5
	7. (location = Pärnu, vehicle = Car): return 3
	8. (location = Pärnu, vehicle = Scooter): return 2.5
	9. (location = Pärnu, vehicle = Bike): return 2
	10. (location = Tallinn-Harku , vehicle = ""): return -200
	11. (location = Tartu-Tõravere, vehicle = ""): return -200
	12. (location = Pärnu, vehicle = ""): return -200
	13. (location = "", vehicle = ""): return -200
	* */
		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_Car () {
		assertEquals(4.0, controller.calculateRegionalBaseFee("Tallinn-Harku", "Car"));
	}

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_Scooter () {
		assertEquals(3.5, controller.calculateRegionalBaseFee("Tallinn-Harku", "Scooter"));
	}

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_Bike () {
		assertEquals(3.0, controller.calculateRegionalBaseFee("Tallinn-Harku", "Bike"));
	}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_Car () {
		assertEquals(3.5, controller.calculateRegionalBaseFee("Tartu-Tõravere", "Car"));
	}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_Scooter () {
		assertEquals(3.0, controller.calculateRegionalBaseFee("Tartu-Tõravere", "Scooter"));
	}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_Bike () {
		assertEquals(2.5, controller.calculateRegionalBaseFee("Tartu-Tõravere", "Bike"));
	}

		@Test
		void testCalculateRegionalBaseFee_Parnu_Car () {
		assertEquals(3.0, controller.calculateRegionalBaseFee("Pärnu", "Car"));
	}

		@Test
		void testCalculateRegionalBaseFee_Parnu_Scooter () {
		assertEquals(2.5, controller.calculateRegionalBaseFee("Pärnu", "Scooter"));
	}

		@Test
		void testCalculateRegionalBaseFee_Parnu_Bike () {
		assertEquals(2.0, controller.calculateRegionalBaseFee("Pärnu", "Bike"));
	}

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_EmptyVehicle () {
		assertEquals(-200.0, controller.calculateRegionalBaseFee("Tallinn-Harku", ""));
	}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_EmptyVehicle () {
		assertEquals(-200.0, controller.calculateRegionalBaseFee("Tartu-Tõravere", ""));
	}

		@Test
		void testCalculateRegionalBaseFee_Parnu_EmptyVehicle () {
		assertEquals(-200.0, controller.calculateRegionalBaseFee("Pärnu", ""));
	}

		@Test
		void testCalculateRegionalBaseFee_EmptyLocationAndVehicle () {
		assertEquals(-200.0, controller.calculateRegionalBaseFee("", ""));
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
			assertEquals(4, controller.determineWeatherSeverity("Hail"));
		}
		@Test
		void testdetermineWeatherSeverity_Moderate_snow_shower() {
			assertEquals(3, controller.determineWeatherSeverity("Moderate snow shower"));
		}
		@Test
		void testdetermineWeatherSeverity_Light_rain() {
			assertEquals(2, controller.determineWeatherSeverity("Light rain"));
		}
		@Test
		void testdetermineWeatherSeverity_EmptyString() {
			assertEquals(1, controller.determineWeatherSeverity(""));
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

		assertEquals(1, controller.calculateExtraFees(station,"Bike"));
	}
	@Test
	void testcalculateExtraFees_Bike_tempMinus20_phenomenonSnow_WindSpeed25() {
		WeatherInput station = new WeatherInput("Pärnu",41803,-20.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));

		assertEquals(-1, controller.calculateExtraFees(station,"Bike"));
	}
	@Test
	void testcalculateExtraFees_Bike_temp5_phenomenonRain_WindSpeed3() {
		WeatherInput station = new WeatherInput("Pärnu",41803,5.0f,3.0f,"Light rain",new Timestamp(System.currentTimeMillis()));

		assertEquals(0.5f, controller.calculateExtraFees(station,"Bike"));
	}
	@Test
	void testcalculateExtraFees_Scooter_phenomenonHail(){
		WeatherInput station = new WeatherInput("Pärnu",41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		assertEquals(-1, controller.calculateExtraFees(station,"Scooter"));
	}
	@Test
	void testcalculateExtraFees_Car(){
		WeatherInput station = new WeatherInput("Pärnu",41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		assertEquals(0, controller.calculateExtraFees(station,"Car"));
	}




	//getStationData(String stationName)
	/*
	1. (stationName = "Pärnu", database has Pärnu): return the station
	2. (stationName = "Pärnu", database does not have Pärnu): return station of "no such station"
	*/
	@Test
	void testGetStationData_Pärnu_Found() {
		// Arrange
		WeatherInput expectedWeatherInput = new WeatherInput("Pärnu", 41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.of(expectedWeatherInput));

		// Act
		WeatherInput result = FeeController.getStationData("Pärnu");

		// Assert
		assertNotNull(result);
		assertEquals(expectedWeatherInput, result);
	}

	@Test
	void testGetStationData_Pärnu_NotFound() {
		// Arrange
		when(weatherRepoMock.findById("NonexistentStation")).thenReturn(Optional.empty());

		// Act
		WeatherInput result = FeeController.getStationData("NonexistentStation");

		// Assert
		assertNotNull(result);
		assertEquals("No Such station", result.getStation_name());
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
		when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.empty());

		// Act
		double fee = controller.getDeliveryFee("Pärnu","Scooter");

		// Assert
		assertEquals(-1, fee);
	}

	@Test
	void testgetDeliveryFee_Pärnu_Bike_phenomenonHail() {
		// Arrange
		WeatherInput weatherInput = new WeatherInput("Pärnu", 41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.of(weatherInput));

		// Act
		double fee = controller.getDeliveryFee("Pärnu","Bike");

		// Assert
		assertEquals(-2, fee);
	}
	@Test
	void testgetDeliveryFee_Pärnu_Car() {
		// Arrange
		WeatherInput weatherInput = new WeatherInput("Pärnu", 41803,5.0f,3.0f,"Hail",new Timestamp(System.currentTimeMillis()));
		when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.of(weatherInput));

		// Act
		double fee = controller.getDeliveryFee("Pärnu","Car");

		// Assert
		assertEquals(3, fee);
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
		when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.empty());

		// Act
		String response = controller.getFeeRequestResponse("Pärnu","Car");

		// Assert
		assertEquals(response, "There was an issue with loading weather data. Check your internet connection.");
	}
	@Test
	void testgetFeeRequestResponse_Pärnu_Bike_WindSpeed25() {
		// Arrange
		WeatherInput station = new WeatherInput("Pärnu",41803,-5.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));
		when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.of(station));

		// Act
		String response = controller.getFeeRequestResponse("Pärnu","Bike");

		// Assert
		assertEquals(response, "Usage of selected vehicle type is forbidden");
	}
	@Test
	void testgetFeeRequestResponse_EmptyLocationAndVehicle() {
		// Arrange
		//WeatherInput station = new WeatherInput("Pärnu",41803,-5.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));
		//when(weatherRepoMock.findById("Pärnu")).thenReturn(Optional.of(station));

		// Act
		String response = controller.getFeeRequestResponse("","");

		// Assert
		assertEquals(response, "Enter city name and vehicle before submitting.");
	}
	@Test
	void testgetFeeRequestResponse_Tallinn_Car() {
		// Arrange
		WeatherInput station = new WeatherInput("Tallinn-Harku",41803,-5.0f,25.0f,"Moderate snow shower",new Timestamp(System.currentTimeMillis()));
		when(weatherRepoMock.findById("Tallinn-Harku")).thenReturn(Optional.of(station));

		// Act
		String response = controller.getFeeRequestResponse("Tallinn-Harku","Car");

		// Assert
		assertEquals(response, "The fee for this delivery is 4.0€.");
	}


	}
@SpringBootTest
class CronJobServiceTest {

	@Mock
	private WeatherRepo weatherRepoMock;


	@Test
	void testupdateDatabase_validURL() throws Exception {
		//Tests that if updateDatabase has been called with a valid URL, then:
		//1. Old weather records were wiped(avoids data duplication and storing of unnecessary data)
		//2. 3 WeatherInput objects were saved into the database
		//3. at least one of these objects had the station name of "Tallinn-Harku"


		// Call the method to be tested
		CronJobService.updateDatabase(weatherRepoMock, "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
		// Verify that deleteAll() was called
		verify(weatherRepoMock, times(1)).deleteAll();
		ArgumentCaptor<WeatherInput> captor = ArgumentCaptor.forClass(WeatherInput.class);
		verify(weatherRepoMock, times(3)).save(captor.capture());

		// Get the captured WeatherInput objects
		List<WeatherInput> capturedWeatherInputs = captor.getAllValues();

		// Assert that one of the objects saved was by the name of "Tallinn-Harku"
		assertTrue(capturedWeatherInputs.stream()
				.anyMatch(input -> Objects.equals(input.getStation_name(), "Tallinn-Harku")));

	}
	@Test
	void testupdateDatabase_invalidURL() throws Exception {
		//Tests that if updateDatabase has been called with an invalid URL, then:
		//NB! By "invalid URL" is meant a URL, that does not have XML data. This would be the case if the weather data page was corrupted or had been assigned another function to fulfill.
		//1. Old weather records were not wiped(rather than wiping old records, the application would continue working with the latest accessable data)
		//2. no WeatherInput objects were saved into the database


		// Call the method to be tested
		CronJobService.updateDatabase(weatherRepoMock, "https://global.fujitsu/et-ee");
		// Verify that deleteAll() was not called
		verify(weatherRepoMock, times(0)).deleteAll();
		ArgumentCaptor<WeatherInput> captor = ArgumentCaptor.forClass(WeatherInput.class);
		// Verify that no arguments were saved into database
		verify(weatherRepoMock, times(0)).save(captor.capture());


	}



}
