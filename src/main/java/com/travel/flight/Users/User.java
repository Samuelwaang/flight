package com.travel.flight.Users;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.travel.flight.Flights.Flight;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  private String email;
  private String password;
  @Enumerated(EnumType.STRING)
  private Role role;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Flight> flights;

  // public User(long id, String email, String password) {
  //   this.id = id;
  //   this.email = email;
  //   this.password = password;
  // }

  // public long getId() {
  //   return id;
  // }
  // public void setId(long id) {
  //   this.id = id;
  // }
  // public String getEmail() {
  //   return email;
  // }
  // public void setEmail(String email) {
  //   this.email = email;
  // }
  // public String getPassword() {
  //   return password;
  // }
  // public void setPassword(String password) {
  //   this.password = password;
  // }
  // public List<Flight> getFlights() {
  //   return flights;
  // }
  // public void setFlights(List<Flight> flights) {
  //   this.flights = flights;
  // }
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }
  @Override
  public String getUsername() {
    return email;
  }
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
  @Override
  public boolean isEnabled() {
    return true;
  }
}
