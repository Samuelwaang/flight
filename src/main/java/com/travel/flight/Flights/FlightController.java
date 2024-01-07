package com.travel.flight.Flights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/flight")
public class FlightController {
  @Autowired // injects this bean (in this case FlightRepository)
  private FlightRepository repository;
  
  @PostMapping(path = "/post")
  public @ResponseBody String addNewFlight(@RequestParam String airline, @RequestParam int time, 
      @RequestParam double price, @RequestParam String location, @RequestParam String link, 
      @RequestParam String flightStart, @RequestParam String flightDestination) {
    try {
      Flight f = new Flight();
      f.setAirline(airline);
      f.setTime(time);
      f.setPrice(price);
      f.setLocation(location);
      f.setLink(link);
      f.setFlightStart(flightStart);
      f.setFlightDestination(flightDestination);
      
      repository.save(f);
    }
    catch(IllegalArgumentException e) {
      return "incorrect input types";
    }
    catch(Exception e) {
      return "unknown error";
    }
    return "saved";
  }
  
  @GetMapping(path = "/all")
  public @ResponseBody Iterable<Flight> getAllUsers() {
    return repository.findAll();
  }
}
