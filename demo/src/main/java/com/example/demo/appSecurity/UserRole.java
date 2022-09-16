package com.example.demo.appSecurity;

import static com.example.demo.appSecurity.UserPermission.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;

public enum UserRole {

    ADMIN(new HashSet(Arrays.asList(COURSE_READ, COURSE_WRITE, STUDENT_READ, STUDENT_WRITE))),
    ADMIN_TRAINEE(new HashSet(Arrays.asList(COURSE_READ, STUDENT_READ))),
    STUDENT(new HashSet(Arrays.asList(COURSE_READ, STUDENT_READ, STUDENT_WRITE)));

    private final Set<UserPermission> userPermissions;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    UserRole(Set<UserPermission> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public Set<UserPermission> getUserPermissions() {
        return userPermissions;
    }

    public final Set<SimpleGrantedAuthority> getAuthority() {
        Set<SimpleGrantedAuthority> authorities = userPermissions.stream()
                .map((permission) -> new SimpleGrantedAuthority((String) permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
