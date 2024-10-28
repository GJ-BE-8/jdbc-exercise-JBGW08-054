package com.nhnacademy.jdbc.student.repository;

import com.nhnacademy.jdbc.common.Page;
import com.nhnacademy.jdbc.student.domain.Student;

import java.sql.Connection;
import java.util.Optional;

public interface StudentRepository {

    int save(Connection connection, Student student);

    Optional<Student> findById(Connection connection, String id);

    int update(Connection connection, Student student);

    int deleteById(Connection connection, String id);

    int deleteAll(Connection connection);

    //todo#2 [추가] totalCount 전체 row 갯수를 구합니다.
    long totalCount(Connection connection);

    //todo#3 [추가] 페이징처리된 결과를 반환 합니다.
    Page<Student> findAll(Connection connection, int page, int pageSize);
}