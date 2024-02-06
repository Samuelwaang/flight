package com.travel.flight.Users;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping(value = "/authenticate", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        AuthenticationRequest request = new AuthenticationRequest(email, password);
        AuthenticationResponse authenticationResponse = service.authenticate(request);

        // Check if authentication is successful
        if (authenticationResponse.getToken() != null) {
            // Add the token as a cookie in the response
            Cookie cookie = new Cookie("jwtToken", authenticationResponse.getToken());
            cookie.setMaxAge(24 * 60 * 60); // Set the expiration time in seconds (e.g., 24 hours)
            cookie.setHttpOnly(true); // Make the cookie accessible only through HTTP
            cookie.setPath("/"); // Set the cookie path as needed
            response.addCookie(cookie);
        }

        return ResponseEntity.ok(authenticationResponse);
    }
}
