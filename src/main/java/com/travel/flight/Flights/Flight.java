package com.travel.flight.Flights;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Flight {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  private String airline;
  private int time; // minutes
  private double price;
  @Column(columnDefinition = "JSON")
  private String location;
  private String link;
  private String flightStart;
  private String flightDestination;
  
  public Flight() {}

  public Flight(long id, String airline, int time, double price, String location, String link, 
      String flightStart, String flightDestination) {
    this.id = id;
    this.airline = airline;
    this.time = time;
    this.price = price;
    this.location = location;
    this.link = link;
    this.flightStart = flightStart;
    this.flightDestination = flightDestination;
  }
  
  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getAirline() {
    return airline;
  }
  public void setAirline(String airline) {
    this.airline = airline;
  }
  public int getTime() {
    return time;
  }
  public void setTime(int time) {
    this.time = time;
  }
  public double getPrice() {
    return price;
  }
  public void setPrice(double price) {
    this.price = price;
  }
  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    this.location = location;
  }
  public String getLink() {
    return link;
  }
  public void setLink(String link) {
    this.link = link;
  }
  public String getFlightStart() {
    return flightStart;
  }
  public void setFlightStart(String flightStart) {
    this.flightStart = flightStart;
  }
  public String getFlightDestination() {
    return flightDestination;
  }
  public void setFlightDestination(String flightDestination) {
    this.flightDestination = flightDestination;
  }
  
  @Override
  public String toString() {
    return "Flight [id=" + id + ", airline=" + airline + ", time=" + time + ", price=" + price + ", location=" + location
        + ", link=" + link + ", flightStart=" + flightStart + ", flightDestination="
        + flightDestination + "]";
  }
}
