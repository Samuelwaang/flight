package com.travel.flight.Flights.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.travel.flight.Flights.FlightDataReceiverService;
import com.travel.flight.Flights.FlightRepository;
import com.travel.flight.Flights.FlightSaveService;
import com.travel.flight.Flights.DTO.Flight;
import com.travel.flight.Flights.DTO.PriceThresholdRequest;
import com.travel.flight.Flights.DTO.SearchQuery;
import com.travel.flight.Flights.DTO.Stop;
import com.travel.flight.Security.JwtProvider;
import com.travel.flight.Users.UserEntity;
import com.travel.flight.Users.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/flight")
public class FlightController {

	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FlightDataReceiverService flightDataReceiverService;
	@Autowired
	private FlightSaveService flightSaveService;
	@Autowired
	private JwtProvider jwtProvider;

	@PostMapping(path = "/post")
	public @ResponseBody String addNewFlight(@RequestParam String airline, @RequestParam int time,
			@RequestParam double price, @RequestParam String link,
			@RequestParam String flightStart, @RequestParam String flightDestination, @RequestParam String leaveTime,
			@RequestParam String arrivalTime, @RequestParam String leaveDate,
			@RequestParam String returnDay, @RequestParam String stops, @RequestParam int numStops,
			@RequestParam String flightImpactLink) {
		try {
			String[] stopsArray = stops.split(";");
			List<Stop> stopList = new ArrayList<>();
			for (String eachStop : stopsArray) {
				String[] stopData = eachStop.split("-");
				Stop stop = new Stop();
				stop.setLocation(stopData[0]);
				stop.setTime(Integer.parseInt(stopData[1]));
				stopList.add(stop);
			}

			Flight f = new Flight();
			f.setAirline(airline);
			f.setTime(time);
			f.setPrice(price);
			f.setLink(link);
			f.setFlightStart(flightStart);
			f.setFlightDestination(flightDestination);
			f.setLeaveTime(leaveTime);
			f.setArrivalTime(arrivalTime);
			f.setLeaveDate(leaveDate);
			f.setReturnDay(returnDay);
			f.setStops(stopList);
			f.setNumStops(numStops);
			f.setFlightImpactLink(flightImpactLink);

			flightRepository.save(f);
		} catch (IllegalArgumentException e) {
			return "incorrect input types";
		} catch (Exception e) {
			e.printStackTrace();
			return "unknown error";
		}
		return "saved";
	}

	@DeleteMapping(path = "/delete/{id}")
	public void deleteById(@PathVariable("id") long id) {
		try {
			flightRepository.deleteById(id);
		} catch (Exception e) {
			System.out.println("error");
		}
		System.out.println("id: " + " sucessfully deleted");
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<Flight> getAllFlights() {
		try {
			return flightRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping(path = "/paged")
	public @ResponseBody Page<Flight> getAllFlightsByPage(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			return flightRepository.findAll(pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Page.empty();
	}

	@PostMapping("/addFlightToUser/{flightId}")
	public @ResponseBody ResponseEntity<String> addFlightToUser(@PathVariable(name = "flightId") long flightId,
			HttpServletRequest request) {

		UserEntity user = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("jwt".equals(cookie.getName())) {
					String token = cookie.getValue();
					if (jwtProvider.validateToken(token)) {
						String email = jwtProvider.getEmailFromJWT(token);
						Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
						user = optionalUser.get();
					}
				}
			}
		}
		Optional<Flight> possibleFlight = flightRepository.findById(flightId);
		Flight flight = possibleFlight.get();

		// update flight list for user
		if (user == null) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
		Set<Flight> flights = user.getFlights();
		flights.add(flight);
		user.setFlights(flights);
		userRepository.save(user);

		// update user list for flight
		Set<UserEntity> users = flight.getUsers();
		users.add(user);
		flight.setUsers(users);
		flightRepository.save(flight);

		return ResponseEntity.ok("User successfully added flight");
	}

	@GetMapping(path = "flightsByUser")
	public @ResponseBody String getAllFlightsByUser(@RequestBody Map<String, String> requestBody) {
		String userId = requestBody.get("userId");
		long userId1 = Long.parseLong(userId);
		Optional<UserEntity> possibleUser = userRepository.findById(userId1);
		UserEntity user = possibleUser.get();

		Set<Flight> flights = user.getFlights();

		System.out.println(user.getFlights());

		return flights.toString();
	}

	@PostMapping("/{id}")
	public @ResponseBody ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flightDetails)
			throws Exception {
		Optional<Flight> optionalFlight = flightRepository.findById(id);
		if (optionalFlight.isPresent()) {
			Flight existingFlight = optionalFlight.get();

			existingFlight.setAirline(flightDetails.getAirline());
			existingFlight.setTime(flightDetails.getTime());
			existingFlight.setPrice(flightDetails.getPrice());
			existingFlight.setLink(flightDetails.getLink());
			existingFlight.setFlightStart(flightDetails.getFlightStart());
			existingFlight.setFlightDestination(flightDetails.getFlightDestination());
			existingFlight.setLeaveTime(flightDetails.getLeaveTime());
			flightRepository.save(existingFlight);

			return ResponseEntity.ok(existingFlight);
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/call")
	public @ResponseBody ResponseEntity<List<Flight>> callApiAndSave(@RequestBody String jsonBody) throws IOException {
		String apiUrl = "http://localhost:8082/data/get";
		List<Flight> flights = flightDataReceiverService.callExternalApi(apiUrl, jsonBody);
		List<Flight> savedFlights = flightSaveService.saveFlights(flights);

		for (Flight flight : flights) {
			for (Stop stop : flight.getStops()) {
				stop.setFlight(flight);
			}
		}

		return ResponseEntity.ok(savedFlights);
	}

	@Transactional
	@DeleteMapping("/deleteAll")
	public ResponseEntity<String> deleteAllFlights() {
		try {
			// clear associations before deleting
			Iterable<UserEntity> users = userRepository.findAll();
			for (UserEntity user : users) {
				user.getFlightPriceThresholds().clear();
			}
			userRepository.saveAll(users);
			flightRepository.deleteAll();

			return ResponseEntity.ok("All flights and associations deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("An error occurred while deleting flights: " + e.getMessage());
		}
	}

	@PostMapping("/set-price-threshold")
	public ResponseEntity<String> setPriceThreshold(@RequestBody PriceThresholdRequest request,
			@RequestParam Long userId) {

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Flight flight = flightRepository.findById(request.getFlightId())
				.orElseThrow(() -> new RuntimeException("Flight not found"));

		user.getFlightPriceThresholds().put(flight, request.getDesiredPrice());
		userRepository.save(user);

		return ResponseEntity.ok("Price threshold set successfully for flight ID " + request.getFlightId());
	}

	@Transactional
	@DeleteMapping("/deleteFlightsByDate")
	public ResponseEntity<String> deleteFlightsByDate(@RequestParam String leaveDate, @RequestParam String returnDay) {
		try {
			List<Flight> flightsToDelete = flightRepository.findByLeaveDateAndReturnDay(leaveDate, returnDay);

			if (flightsToDelete.isEmpty()) {
				return ResponseEntity.ok("No flights found with the specified leaveDate and returnDay.");
			}

			// clear associations for each flight
			List<UserEntity> users = userRepository.findAll();
			for (UserEntity user : users) {
				user.getFlights().removeAll(flightsToDelete);
			}
			userRepository.saveAll(users);

			// delete flights
			flightRepository.deleteAll(flightsToDelete);

			return ResponseEntity.ok("Flights with the specified leaveDate and returnDay deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("An error occurred while deleting flights: " + e.getMessage());
		}
	}

	@GetMapping("/search")
	public List<Flight> getFlights(@RequestParam String flightStart,
			@RequestParam String flightDestination,
			@RequestParam String leaveDate,
			@RequestParam String returnDay) {
		return flightRepository.findByFlightStartAndFlightDestinationAndLeaveDateAndReturnDay(
				flightStart, flightDestination, leaveDate, returnDay);
	}

	@GetMapping("/cheapest")
	public ResponseEntity<List<Flight>> getCheapestFlights(
			@RequestParam String flightStart,
			@RequestParam String flightDestination) {
		Pageable pageable = PageRequest.of(0, 50);
		List<Flight> flights = flightRepository.findCheapestFlights(flightStart, flightDestination, pageable);
		if (flights.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.ok(flights);
		}
	}

	@GetMapping("/cheapest-by-airline")
	public List<Flight> getCheapestFlights(
			@RequestParam String flightStart,
			@RequestParam String flightDestination,
			@RequestParam(required = false) List<String> airlines) {
		Pageable pageable = PageRequest.of(0, 50);
		return flightRepository.findFlightsWithRegexp(flightStart, flightDestination, "UNITED", pageable);
	}

	@GetMapping("/search-filtered")
	public ResponseEntity<List<Flight>> getFilteredFlights(@RequestBody SearchQuery request) {
		String searchQuery = "SELECT * FROM flight WHERE flightStart = " + request.getFlightStart()
				+ " AND flightDestination = " + request.getFlightDestination();
		if (request.getAirlines().isPresent()) {
			searchQuery += " AND airline REGEXP ";
			List<String> airlines = request.getAirlines().get();
			for (int i = 0; i < airlines.size(); i++) {
				if (i == 0) {
					searchQuery += airlines.get(i);
				} else {
					searchQuery += "|" + airlines.get(i);
				}
			}
		}
		if (request.getMinPrice().isPresent() && request.getMaxPrice().isPresent()) {
			searchQuery += " AND price BETWEEN " + request.getMinPrice() + " and " + request.getMaxPrice();
		}
		if (request.getMaxTime().isPresent()) {
			searchQuery += " AND time < " + request.getMaxTime();
		}
		if (request.getMaxStops().isPresent()) {
			searchQuery += " AND numStops < " + request.getMaxStops();
		}
		if (request.getStopLength().isPresent()) {
			searchQuery += " AND stopLength < " + request.getStopLength();
		}
		if (request.getTripLength().isPresent()) {
			searchQuery += " AND time < " + request.getTripLength();
		}
		if (request.getCarryOnAllowed().isPresent()) {
			searchQuery += " AND carryOnAllowed = TRUE";
		}

		searchQuery += " ORDER BY price ASC";
		return ResponseEntity.ok(flightRepository.findCheapestFlightsByQuery(searchQuery));
	}

}
