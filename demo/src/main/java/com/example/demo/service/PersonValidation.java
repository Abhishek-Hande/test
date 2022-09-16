package com.example.demo.service;

import com.example.demo.entity.Person;

public class PersonValidation {
    boolean isnameValid(String name){
        return name.length()>0;
    }
    boolean isAddressValid(String address){
        return address.length()>0;
    }
    boolean isAdult(int age){
        return age>16;
    }
   public boolean isValid(Person person){

        return isnameValid(person.getName())&&isAddressValid(person.getAddress())&&isAdult(Integer.parseInt(person.getAge()));

    }

}
