package com.example.Courier;

	//CalculateRegionalBaseFee(String location, String vehicle) tests
	/*
	1. (location = Tallinn-Harku , vehicle = Car) => return 4
	2. (location = Tallinn-Harku, vehicle = Scooter) => return 3.5
	3. (location = Tallinn-Harku, vehicle = Bike) => return 3
	4. (location = Tartu-Tõravere, vehicle = Car) => return 3.5
	5. (location = Tartu-Tõravere, vehicle = Scooter) => return 3
	6. (location = Tartu-Tõravere, vehicle = Bike) => return 2.5
	7. (location = Pärnu, vehicle = Car) => return 3
	8. (location = Pärnu, vehicle = Scooter) => return 2.5
	9. (location = Pärnu, vehicle = Bike) => return 2
	10. (location = Tallinn-Harku , vehicle = "") => return -200
	11. (location = Tartu-Tõravere, vehicle = "") => return -200
	12. (location = Pärnu, vehicle = "") => return -200
	13. (location = "", vehicle = "") => return -200
	* */
	//determineWeatherSeverity(String phenomenon)

	//calculateExtraFees(WeatherInput station, String vehicle)
	/*
	1.bike, temp is -5, weather 1, windspeed 15 return 1
	2.bike, temp is -20, weather 2, windspeed 25 return -1
	3.bike, temp is 5, weather 3, windspeed 3 return 1
	4.scooter, weather 4 return -1
	5.car, return 0
	*/
	//getStationData(String stationName)
	/*
	1. stationName = "Pärnu", database has Pärnu: return the station
	2. stationName = "Pärnu", database does not have Pärnu: return station of "no such station"
	*/
	//getDeliveryFee(String location, String vehicle)
	/*
	1. location = "Pärnu", database does not have Pärnu: return -3
	2. location = "Pärnu", vehicle = bike, weatherSeverity = 4: return -2
	3. location = "Pärnu", vehicle = car: return 3
	 */
	//getFeeRequestResponse(String location, String vehicle)
	/*
	1. location = "Pärnu", database empty: return "There was an issue with loading weather data. Check your internet connection."
	2. vehicle = "Bike", windspeed = 25: return "Usage of selected vehicle type is forbidden"
	3. location = "": return "Enter city name and vehicle before submitting."
	4. location = "Tallinn-Harku", vehicle = car: return "The fee for this delivery is 4€."
	*/


import com.example.Courier.controller.FeeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

	@SpringBootTest
	class FeeControllerTest {

		@Autowired
		private FeeController controller;

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_Car() {
			assertEquals(4.0, controller.calculateRegionalBaseFee("Tallinn-Harku", "Car"));
		}

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_Scooter() {
			assertEquals(3.5, controller.calculateRegionalBaseFee("Tallinn-Harku", "Scooter"));
		}

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_Bike() {
			assertEquals(3.0, controller.calculateRegionalBaseFee("Tallinn-Harku", "Bike"));
		}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_Car() {
			assertEquals(3.5, controller.calculateRegionalBaseFee("Tartu-Tõravere", "Car"));
		}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_Scooter() {
			assertEquals(3.0, controller.calculateRegionalBaseFee("Tartu-Tõravere", "Scooter"));
		}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_Bike() {
			assertEquals(2.5, controller.calculateRegionalBaseFee("Tartu-Tõravere", "Bike"));
		}

		@Test
		void testCalculateRegionalBaseFee_Parnu_Car() {
			assertEquals(3.0, controller.calculateRegionalBaseFee("Pärnu", "Car"));
		}

		@Test
		void testCalculateRegionalBaseFee_Parnu_Scooter() {
			assertEquals(2.5, controller.calculateRegionalBaseFee("Pärnu", "Scooter"));
		}

		@Test
		void testCalculateRegionalBaseFee_Parnu_Bike() {
			assertEquals(2.0, controller.calculateRegionalBaseFee("Pärnu", "Bike"));
		}

		@Test
		void testCalculateRegionalBaseFee_TallinnHarku_EmptyVehicle() {
			assertEquals(-200.0, controller.calculateRegionalBaseFee("Tallinn-Harku", ""));
		}

		@Test
		void testCalculateRegionalBaseFee_TartuToravere_EmptyVehicle() {
			assertEquals(-200.0, controller.calculateRegionalBaseFee("Tartu-Tõravere", ""));
		}

		@Test
		void testCalculateRegionalBaseFee_Parnu_EmptyVehicle() {
			assertEquals(-200.0, controller.calculateRegionalBaseFee("Pärnu", ""));
		}

		@Test
		void testCalculateRegionalBaseFee_EmptyLocationAndVehicle() {
			assertEquals(-200.0, controller.calculateRegionalBaseFee("", ""));
		}


		/*
	1. (phenomenon = Hail) => return 4
	2. (phenomenon = Moderate snow shower) => return 3
	3. (phenomenon = Light rain) => return 2
	4. (phenomenon = "") => return 1
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

	}