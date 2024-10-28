package com.nhnacademy.jdbc.student.repository;

import com.nhnacademy.jdbc.student.domain.Student;

import java.util.Optional;

public interface StudentRepository {
    int save(Student student);

    Optional<Student> findById(String id);

    int update(Student student);

    int deleteById(String id);
}