package com.example.demo.repository;

import java.lang.annotation.Native;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Person;

public interface PersonRepo extends JpaRepository<Person,Integer> {
    // @Query(value = "select *  from Student",nativeQuery = true)
    // Page<Person> findStudentWithPaggination(Page page);
}
