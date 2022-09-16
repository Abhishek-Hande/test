package com.example.demo.entity;
//package com.example.Spring_Security_1.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
// @JsonIgnoreProperties("student_name")
@JsonInclude(value = Include.NON_EMPTY)

public class StudentEntity {
    @Id
    @Column(name = "age")
    private int age;
    @JsonProperty(value = "student_name")
    // @Column(name = "Name")
    @Transient
    private String name;
    // @JsonProperty(access = Access.READ_ONLY)
    // @JsonProperty(access = Access.WRITE_ONLY)
    // @JsonIgnore
    // @JsonRawValue
    private String address;

    public StudentEntity(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public StudentEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "StudentEntity [address=" + address + ", age=" + age + ", name=" + name + "]";
    }

}
