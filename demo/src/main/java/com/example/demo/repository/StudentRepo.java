package com.example.demo.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.StudentEntity;

import net.bytebuddy.TypeCache.Sort;

public interface StudentRepo extends JpaRepository<StudentEntity,Integer>{
    // @Query(value = "select *  from student_entity",nativeQuery = true)
    // Page<StudentEntity> findStudentWithPagginationSort(org.springframework.data.domain.Sort sort,Pageable page);

    @Query(value = "select *  from student_entity",nativeQuery = true)
    Page<StudentEntity> findStudentWithPaggination(Pageable page);
    @Query(value = "select *  from student_entity",nativeQuery = true)
   // Page findStudentPageSory(org.springframework.data.domain.Sort sort, Pageable page);
    Page findStudentPageSory(Pageable page);
}