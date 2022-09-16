package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class applicationUserService implements UserDetailsService {
    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public applicationUserService(@Qualifier("abhi") ApplicationUserDao fackApplicationUserDaoService) {
        this.applicationUserDao = fackApplicationUserDaoService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = applicationUserDao
                .selectApplicationUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Username %s not found in the system", username)));

        return userDetails;
    }

}
