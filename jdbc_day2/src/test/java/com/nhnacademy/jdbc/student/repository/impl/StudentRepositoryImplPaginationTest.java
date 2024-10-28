package com.nhnacademy.jdbc.student.repository.impl;

import com.nhnacademy.jdbc.common.Page;
import com.nhnacademy.jdbc.student.domain.Student;
import com.nhnacademy.jdbc.student.repository.StudentRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
class StudentRepositoryImplPaginationTest {

    static StudentRepository studentRepository = new StudentRepositoryImpl();
    static Connection connection;
    @BeforeAll
    static void setUp() throws SQLException {
        connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);
        Iterator<Integer> intIterator = new Random().ints(20,31).iterator();

        //모두삭제
        studentRepository.deleteAll(connection);

        //1~101 student 생성
        for(int i=1; i<=101; i++){
            String id="student" + i;
            String name="학생" + i;
            Student.GENDER gender = i%2==0 ? Student.GENDER.M : Student.GENDER.F;
            Student student = new Student(id,name,gender,intIterator.next());

            studentRepository.save(connection,student);
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.rollback();
        connection.close();
    }

    @Test
    @DisplayName("전체 로우 수")
    void totalCount() {
        long actual = studentRepository.totalCount(connection);
        Assertions.assertEquals(101,actual);
    }

    @ParameterizedTest(name = "{index} page:{0}, rows:{1} ")
    @MethodSource("paginationArguments")
    @DisplayName("pagination - totalrows : 101")
    void findAll(int page, int rows) {
        //ai 추가하기
        Page<Student> studentPage = studentRepository.findAll(connection,page,10);
        for(Student student : studentPage.getContent()){
            log.debug("student:{}",student);
        }
        Assertions.assertEquals(rows,studentPage.getContent().size());
    }

    // Arguments.of(page, rowCount), 11 page의 rowCount =1
    private static Stream<? extends Arguments> paginationArguments(){
        return Stream.of(
                Arguments.of(1,10),
                Arguments.of(2,10),
                Arguments.of(3,10),
                Arguments.of(4,10),
                Arguments.of(5,10),
                Arguments.of(6,10),
                Arguments.of(7,10),
                Arguments.of(8,10),
                Arguments.of(9,10),
                Arguments.of(10,10),
                Arguments.of(11,1)
        );
    }

}