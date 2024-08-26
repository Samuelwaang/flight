	package com.travel.flight.Flights;

	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import java.util.Set;

	import org.springframework.beans.factory.annotation.Autowired;
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

	import com.travel.flight.Users.UserEntity;
	import com.travel.flight.Users.UserRepository;

	import jakarta.transaction.Transactional;

@Controller
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

	
	@PostMapping(path = "/post")
	public @ResponseBody String addNewFlight(@RequestParam String airline, @RequestParam int time, 
		@RequestParam double price, @RequestParam String link, 
		@RequestParam String flightStart, @RequestParam String flightDestination, @RequestParam String leaveTime, @RequestParam String arrivalTime, @RequestParam String leaveDate, 
			@RequestParam String returnDay, @RequestParam String stops, @RequestParam int numStops, @RequestParam String flightImpactLink) {
		try {
		String[] stopsArray = stops.split(";");
		List<Stop> stopList = new ArrayList<>();
		for(String eachStop : stopsArray) {
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
		}
		catch(IllegalArgumentException e) {
		return "incorrect input types";
		}
		catch(Exception e) {
		e.printStackTrace();
		return "unknown error";
		}
		return "saved";
	}

	@DeleteMapping(path = "/delete/{id}")
	public void deleteById(@PathVariable("id") long id) {
		try {
			flightRepository.deleteById(id);  
		}
		catch(Exception e) {
			System.out.println("error");
		}
		System.out.println("id: " + " sucessfully deleted");
	}
	
	@GetMapping(path = "/all")
	public @ResponseBody Iterable<Flight> getAllUsers() {
		try {
			return flightRepository.findAll();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	@PostMapping("/addFlightToUser/{flightId}/{userId}")
	public @ResponseBody String addFlightToUser(@PathVariable(name = "flightId") long flightId, @PathVariable(name = "userId") long userId) {
		Optional<UserEntity> possibleUser = userRepository.findById(userId);    
		UserEntity user = possibleUser.get();
		System.out.println(user.getEmail());
		Optional<Flight> possibleFlight = flightRepository.findById(flightId);   
		Flight flight = possibleFlight.get();
		System.out.println(flight.getAirline()); 

		// update flight list for user
		Set<Flight> flights = user.getFlights();
		flights.add(flight);
		user.setFlights(flights);
		userRepository.save(user);

		// update user list for flight
		Set<UserEntity> users = flight.getUsers();
		users.add(user);
		flight.setUsers(users);
		flightRepository.save(flight);

		return "flight saved";
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
	public @ResponseBody ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flightDetails) throws Exception {
		Optional<Flight> optionalFlight = flightRepository.findById(id);
		if(optionalFlight.isPresent()) {
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
		} 
		catch (Exception e) {
			return ResponseEntity.status(500).body("An error occurred while deleting flights: " + e.getMessage());
		}
	}

	@PostMapping("/set-price-threshold")
	public ResponseEntity<String> setPriceThreshold(@RequestBody PriceThresholdRequest request, @RequestParam Long userId) {

		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Flight flight = flightRepository.findById(request.getFlightId())
				.orElseThrow(() -> new RuntimeException("Flight not found"));

		user.getFlightPriceThresholds().put(flight, request.getDesiredPrice());
		userRepository.save(user);

		return ResponseEntity.ok("Price threshold set successfully for flight ID " + request.getFlightId());
	}
} 
