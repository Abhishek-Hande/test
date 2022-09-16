package com.example.demo.appSecurity;

import java.security.Key;

import java.util.concurrent.TimeUnit;

import org.aspectj.weaver.ast.And;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.example.demo.auth.*;
import com.example.demo.jwt.JwtTokenFilter;
import com.example.demo.jwt.jwtUsernameAndPasswordAuthenticateAndThenFilter;

import ch.qos.logback.core.util.TimeUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class springSecurity extends WebSecurityConfigurerAdapter {

    private final applicationUserService applicationUserService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public springSecurity(PasswordEncoder passwordEncoder, applicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new jwtUsernameAndPasswordAuthenticateAndThenFilter(authenticationManager()))
                .addFilterAfter(new JwtTokenFilter(), jwtUsernameAndPasswordAuthenticateAndThenFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/v1/student/persons/**").hasRole(UserRole.STUDENT.name())

                // .antMatchers(HttpMethod.DELETE,"/api/v1/managment/persons/**").hasAuthority(UserPermission.STUDENT_WRITE.getPermission())
                // .antMatchers(HttpMethod.PUT,"/api/v1/managment/persons/**").hasAuthority(UserPermission.STUDENT_WRITE.getPermission())
                // .antMatchers(HttpMethod.POST,"/api/v1/managment/persons/**").hasAuthority(UserPermission.STUDENT_WRITE.getPermission())
                // .antMatchers("/api/v1/managment/persons/**").hasAnyAuthority("ROLE_"+UserRole.ADMIN.name(),"ROLE_"+UserRole.ADMIN_TRAINEE.name())
                // .antMatchers("/api/v1/managment/persons/*").hasRole(UserRole.ADMIN.name())
                // .antMatchers("/api/v1/managment/persons/**").hasRole(UserRole.ADMIN_TRAINEE.name())
                .anyRequest()
                .authenticated();
        // .and()
        // .formLogin()
        // .loginPage("/login")
        // .permitAll()
        // .defaultSuccessUrl("/courses",true)
        // .and()
        // .rememberMe()
        // .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21)) //to customise
        // dw=efault 14 days exp to 21 days
        // .key("veryStrongSecure") //to generate token specify the key
        // //.rememberMeParameter("remember-me")
        // .userDetailsService(applicationUserService)
        // .and()
        // .logout()
        // .logoutUrl("/logout")
        // .clearAuthentication(true)
        // .invalidateHttpSession(true)
        // .deleteCookies("JSESSIONID","remember-me")
        // .logoutSuccessUrl("/login");

    }

    @Bean
    public DaoAuthenticationProvider DaoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(DaoAuthenticationProvider());
    }

}
