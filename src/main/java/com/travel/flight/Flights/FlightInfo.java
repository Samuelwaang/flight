package com.travel.flight.Flights;

public class FlightInfo {
  private String info;
  
  public FlightInfo(String info) {
    this.info = info;
  }

  public void add(String location, String time) {
    if(info.isEmpty()) {
      info += location + ";" + time;  
    }
    info += ";" + location + ";" + time;
  }
  
  public String toString() {
    return info;
  }
}
