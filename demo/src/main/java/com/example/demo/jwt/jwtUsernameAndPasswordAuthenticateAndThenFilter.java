package com.example.demo.jwt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class jwtUsernameAndPasswordAuthenticateAndThenFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager; 
  public jwtUsernameAndPasswordAuthenticateAndThenFilter(AuthenticationManager authenticationManager){
    this.authenticationManager=authenticationManager;
  }
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
          throws AuthenticationException {
            try {
              usernamePasswordAuthenticationRequest authenticationRequest=new ObjectMapper().readValue(request.getInputStream(),usernamePasswordAuthenticationRequest.class);
              Authentication authentication=new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
              Authentication authenticate=authenticationManager.authenticate(authentication);
              return authenticate;
            }  catch (IOException e) {
              throw new RuntimeException(e);
              //e.printStackTrace();
            }
     // return super.attemptAuthentication(request, response);
  }
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
          Authentication authResult) throws IOException, ServletException {
            String key="MostSecureKeyInTheUniverseMostSecureKeyInTheUniverseMostSecureKeyInTheUniverseMostSecureKeyInTheUniverse";
      String token =Jwts.builder()
            .setSubject(authResult.getName())
            .claim("authorities",authResult.getAuthorities())
            .setIssuedAt(new Date())
            .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(4)))
            .signWith(Keys.hmacShaKeyFor(key.getBytes()))
            .compact();
            response.addHeader("Authorization", token);
     // super.successfulAuthentication(request, response, chain, authResult);
  }  
}
