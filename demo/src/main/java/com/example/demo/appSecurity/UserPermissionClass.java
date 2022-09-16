package com.example.demo.appSecurity;

import java.util.List;

import org.springframework.stereotype.Component;

@Component

public class UserPermissionClass {
    List<String> userPermisionList;

    public UserPermissionClass(List<String> userPermisionList) {
        this.userPermisionList = userPermisionList;
    }

}
