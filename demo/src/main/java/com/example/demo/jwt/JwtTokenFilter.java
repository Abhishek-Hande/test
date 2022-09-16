package com.example.demo.jwt;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.appSecurity.springSecurity;
import com.google.common.base.Strings;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.extras.springsecurity5.util.SpringSecurityContextUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader=request.getHeader("Authorization");
       // filterChain.doFilter(request, response);
        if(Strings.isNullOrEmpty(authorizationHeader)){
            filterChain.doFilter(request, response);
            return;

        }

       try{
        String key="MostSecureKeyInTheUniverseMostSecureKeyInTheUniverseMostSecureKeyInTheUniverseMostSecureKeyInTheUniverse";
        Jws<Claims>claimsJwt=Jwts.parser()
        .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
        .parseClaimsJws(authorizationHeader);
        Claims body=claimsJwt.getBody();
        String username=body.getSubject();
        List<Map<String,String>> authorities=(List<Map<String, String>>) body.get("authorities");
        Set<SimpleGrantedAuthority>simpleGrantedAuthority=authorities
                                        .stream()
                                        .map((p)->new SimpleGrantedAuthority(p.get("authority")))
                                        .collect(Collectors.toSet());
        Authentication authentication=new UsernamePasswordAuthenticationToken(username,null,simpleGrantedAuthority);
        SecurityContextHolder.getContext().setAuthentication(authentication);
       
        
    }
       catch(Exception e){
           e.printStackTrace();
           throw new BadCredentialsException(String.format("token %s is not trusted",authorizationHeader));

       }
       filterChain.doFilter(request, response);
    }
    
}
