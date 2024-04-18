package com.travel.flight.Flights;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    try{
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
  public String addFlightToUser(@PathVariable(name = "flightId") long flightId, @PathVariable(name = "userId") long userId) {
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


}
