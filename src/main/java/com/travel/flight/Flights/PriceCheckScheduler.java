package com.travel.flight.Flights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.travel.flight.Flights.DTO.Flight;
import com.travel.flight.Users.UserEntity;
import com.travel.flight.Users.UserRepository;

import java.util.Map;

// @Component
public class PriceCheckScheduler {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(fixedRate = 3600000)
    public void checkFlightPrices() {
        for (UserEntity user : userRepository.findAll()) {
            for (Map.Entry<Flight, Double> entry : user.getFlightPriceThresholds().entrySet()) {
                Flight flight = entry.getKey();
                double desiredPrice = entry.getValue();

                if (flight.getPrice() < desiredPrice) {
                    sendNotification(user.getEmail(), flight);
                }
            }
        }
    }

    private void sendNotification(String userEmail, Flight flight) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Price Alert: Flight Price Dropped");
        message.setText("The price for flight " + flight.getAirline() + " from " +
                flight.getFlightStart() + " to " + flight.getFlightDestination() +
                " has dropped to " + flight.getPrice() + ". Book now at: " + flight.getLink());

        mailSender.send(message);
    }
}