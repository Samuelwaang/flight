package com.travel.flight.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // String token = getJwtFromRequest(request);
        // if(StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
        //     String username = tokenProvider.getEmailFromJWT(token);
        //     UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        //     UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
        //             userDetails.getAuthorities());
        //     authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //     SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // }
        // filterChain.doFilter(request, response);

        String token = null;
        String username = null;

        if(request.getCookies() != null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("jwt")){
                    token = cookie.getValue();
                }
            }
        }
        System.out.println("token: " + token);

        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }

        username = tokenProvider.getEmailFromJWT(token);

        if(username != null){
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if(tokenProvider.validateToken(token)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }

        filterChain.doFilter(request, response);
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer: ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
    
}
