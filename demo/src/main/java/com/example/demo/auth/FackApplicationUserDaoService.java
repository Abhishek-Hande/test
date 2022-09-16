package com.example.demo.auth;

import static com.example.demo.appSecurity.UserRole.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository("abhi")
public class FackApplicationUserDaoService implements ApplicationUserDao {

    final PasswordEncoder passwordEncoder;

    @Autowired
    public FackApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    List<ApplicationUser> getApplicationUsers() {
        List<ApplicationUser> applicationUsers = Arrays.asList(
                new ApplicationUser(STUDENT.getAuthority(), passwordEncoder.encode("password"), "Nikita", true, true,
                        true, true),
                new ApplicationUser(ADMIN.getAuthority(), passwordEncoder.encode("password"), "Pappa", true, true, true,
                        true),
                new ApplicationUser(ADMIN_TRAINEE.getAuthority(), passwordEncoder.encode("password"), "Rahul", true,
                        true, true, true));
        return applicationUsers;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username
                        .equals(applicationUser.getUsername()))
                .findFirst();
    }

}
