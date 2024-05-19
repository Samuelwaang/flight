package com.travel.flight.Flights;

import java.util.HashSet;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travel.flight.Users.UserEntity;
import com.travel.flight.Users.UserRepository;

@Controller
@RequestMapping("/flight")
public class FlightController {
  @Autowired // injects this bean (in this case FlightRepository)
  private FlightRepository flightRepository;
  @Autowired
  private UserRepository userRepository;
  
  @PostMapping(path = "/post")
  public @ResponseBody String addNewFlight(@RequestParam String airline, @RequestParam int time, 
      @RequestParam double price, @RequestParam String location, @RequestParam String link, 
      @RequestParam String flightStart, @RequestParam String flightDestination, @RequestParam String leaveTime) {
    try {
      Flight f = new Flight();
      f.setAirline(airline);
      f.setTime(time);
      f.setPrice(price);
      f.setLocation(location);
      f.setLink(link);
      f.setFlightStart(flightStart);
      f.setFlightDestination(flightDestination);
      f.setLeaveTime(leaveTime);

      flightRepository.save(f);
    }
    catch(IllegalArgumentException e) {
      return "incorrect input types";
    }
    catch(Exception e) {
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
    return flightRepository.findAll();
  }

  @PostMapping("/addFlightToUser/{flightId}/{userId}")
  public @ResponseBody String addFlightToUser(@PathVariable(name = "flightId") long flightId, @PathVariable(name = "userId") long userId) {
    Optional<UserEntity> possibleUser = userRepository.findById(userId);    
    UserEntity user = possibleUser.get();
    Optional<Flight> possibleFlight = flightRepository.findById(flightId);    
    Flight flight = possibleFlight.get();

    Set<Flight> flights = new HashSet<>();
    flights.add(flight);
    
    user.setFlights(flights);
    userRepository.save(user);

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
        existingFlight.setLocation(flightDetails.getLocation());
        existingFlight.setLink(flightDetails.getLink());
        existingFlight.setFlightStart(flightDetails.getFlightStart());
        existingFlight.setFlightDestination(flightDetails.getFlightDestination());
        existingFlight.setLeaveTime(flightDetails.getLeaveTime());
        flightRepository.save(existingFlight);

        return ResponseEntity.ok(existingFlight);
      }

      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }





} 
