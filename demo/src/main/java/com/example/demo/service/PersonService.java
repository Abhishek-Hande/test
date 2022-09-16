package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.entity.Person;
import com.example.demo.entity.StudentEntity;

public interface PersonService {
    Person findById(int id);
    List<Person>findByAge(int age);
    Person savePerson(Person p);
    Person modifyPerson(Person p);
    List<Person>findAll();
    Page<StudentEntity>findStudentPage(Pageable page);
    public Page findStudentPageSory(Pageable page);
}
