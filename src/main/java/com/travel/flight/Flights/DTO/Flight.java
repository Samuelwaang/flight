package com.travel.flight.Flights.DTO;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.travel.flight.Users.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private long id;
    private String airline;
    private int time;
    private double price;
    @Column(length = 1024)
    private String link;
    private String flightStart;
    private String flightDestination;
    private String leaveTime;
    private String arrivalTime;
    private String leaveDate;
    private String returnDay;
    private String flightImpactLink;
    private String tripLength;
    private boolean carryOnAllowed;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "flightStops")
    private List<Stop> stops;
    private int numStops;

    // return flight data
    private String returnAirline;
    private String returnLeaveTime;
    private String returnArrivalTime;
    @OneToMany(mappedBy = "returnFlight", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "returnFlightStops")
    private List<Stop> returnStops;
    private int returnNumStops;
    private int returnTime;

    @ManyToMany(mappedBy = "flights", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserEntity> users = new HashSet<>();

    // Getters and setters for users
    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", airline='" + airline + '\'' +
                ", time=" + time +
                ", price=" + price +
                ", link='" + link + '\'' +
                ", flightStart='" + flightStart + '\'' +
                ", flightDestination='" + flightDestination + '\'' +
                ", leaveTime='" + leaveTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", leaveDate='" + leaveDate + '\'' +
                ", returnDay='" + returnDay + '\'' +
                ", tripLength='" + tripLength + '\'' +
                ", carryOnAllowed='" + carryOnAllowed + '\'' +
                ", numStops=" + numStops +
                ", returnAirline='" + returnAirline + '\'' +
                ", returnLeaveTime='" + returnLeaveTime + '\'' +
                ", returnArrivalTime='" + returnArrivalTime + '\'' +
                ", returnNumStops=" + returnNumStops +
                ", returnTime=" + returnTime +
                '}';
    }
}
