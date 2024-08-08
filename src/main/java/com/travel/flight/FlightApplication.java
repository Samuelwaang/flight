package com.travel.flight;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.travel.flight.Flights.FlightInit;


@SpringBootApplication()
public class FlightApplication {
	public static void main(String[] args) {
		SpringApplication.run(FlightApplication.class, args);
	}

    public void run(ApplicationArguments args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(FlightApplication.class);
        FlightInit flightInit = context.getBean(FlightInit.class);
        
        // Example start point and destination
        String startPoint = "JFK";
        String destination = "LAX";
        
        flightInit.init(startPoint, destination);
    }
}
