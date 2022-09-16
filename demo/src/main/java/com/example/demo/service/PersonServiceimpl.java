package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import com.example.demo.entity.Person;
import com.example.demo.entity.StudentEntity;
import com.example.demo.repository.PersonRepo;
import com.example.demo.repository.StudentRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonServiceimpl implements PersonService {

    @Autowired
    PersonRepo personRepo;
    @Autowired
    StudentRepo studentRepo;

    @Override
    public Person findById(int id) {

        return personRepo.findById(id).orElse(null);
    }

    @Override
    public List<Person> findByAge(int age) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Person savePerson(Person p) {
        // TODO Auto-generated method stub
        return personRepo.save(p);
    }

    @Override
    public Person modifyPerson(Person p) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<StudentEntity> findStudentPage(Pageable page) {
        // TODO Auto-generated method stub

        return studentRepo.findStudentWithPaggination(page);
    }

    @Override
    public List<Person> findAll() {
        // TODO Auto-generated method stub
        return personRepo.findAll();
    }

    public void deletePerson(Person toBeDelete) {
        personRepo.delete(toBeDelete);
        ;

    }

    @Override
    public Page findStudentPageSory(Pageable page) {

        return studentRepo.findStudentPageSory(page);
    }

}
