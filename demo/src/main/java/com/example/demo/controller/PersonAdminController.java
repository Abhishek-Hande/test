package com.example.demo.controller;

import static com.example.demo.appSecurity.UserPermissionClass.*;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;

import com.example.demo.appSecurity.UserPermissionClass;
import com.example.demo.appSecurity.UserRole;
import com.example.demo.entity.Person;
import com.example.demo.entity.StudentEntity;
import com.example.demo.service.PersonServiceimpl;

import org.checkerframework.checker.units.qual.Length;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/managment/persons")
public class PersonAdminController {
    @Autowired
    PersonServiceimpl personServiceimpl;

    @GetMapping("/getAllPerson")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADMIN_TRAINEE')")
    ResponseEntity<?> getAllPerson(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(personServiceimpl.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("Error while retriving the person's list", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAuthority('student:write')")
    ResponseEntity<?> findByName(HttpServletRequest request, @PathVariable int id) {
        try {
            return new ResponseEntity<>(personServiceimpl.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("Error while retriving the person detail", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PostMapping("/savePerson")
    @PreAuthorize("hasAuthority('student:write')")
    ResponseEntity<?> addPerson(@RequestBody Person person) {
        try {
            return new ResponseEntity<Person>(personServiceimpl.savePerson(person), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return new ResponseEntity<String>("Error while saving  the person", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @GetMapping("/person/{pageNumber}")
    @PreAuthorize("hasAuthority('student:write')")
    ResponseEntity<?> getStudentWithPaggination1(@PathVariable int pageNumber,
            HttpServletRequest httpServletRequest) {
        try {
            Sort sort = Sort.by(Arrays.asList(new Order(Direction.ASC, "address", NullHandling.NATIVE),
                    new Order(Direction.ASC, "age", NullHandling.NATIVE)));
            Pageable page = PageRequest.of(pageNumber, 10).withSort(sort);
            Page result = personServiceimpl.findStudentPageSory(page);
            return new ResponseEntity<Page<StudentEntity>>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return new ResponseEntity<String>("Error while retriving the studentList", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/deletePerson")
    @PreAuthorize("hasAuthority('student:write')")
    ResponseEntity<String> deletePerson(@RequestParam int id) throws Exception {
        try {
            Person toBeDelete = personServiceimpl.findById(id);
            personServiceimpl.deletePerson(toBeDelete);
            return new ResponseEntity<String>("The person deleted", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new ResponseEntity<String>("Error while deleting the person", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
