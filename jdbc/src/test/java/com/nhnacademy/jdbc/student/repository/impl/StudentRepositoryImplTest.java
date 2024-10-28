package com.nhnacademy.jdbc.student.repository.impl;

import com.nhnacademy.jdbc.student.domain.Student;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class StudentRepositoryImplTest {

    public static StudentRepositoryImpl studentRepository;
    public static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        //connection 얻기
        connection = DbUtils.getDataSource().getConnection();

        studentRepository = new StudentRepositoryImpl();

        Random random = new Random();
        Iterator<Integer> iterator = random.ints(20,50).iterator();
        for(int i=1; i<=10; i++){
            String id="student" + i;
            String name="학생" + i;
            Student.GENDER gender = Student.GENDER.M;
            int age =iterator.next();
            Student student = new Student(id, name, gender,age);
            studentRepository.deleteById(connection,id);
            studentRepository.save(connection,student);
        }

        studentRepository.deleteById(connection,"student100");
    }

    @AfterAll
    static void release() throws SQLException {
        // 사용한 connection은 반납
        if(Objects.nonNull(connection)){
            connection.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("insert student : student 100")
    void save() {
        Student newStudent = new Student("student100","학생100", Student.GENDER.M,30);
        int result = studentRepository.save(connection,newStudent);
        Assertions.assertEquals(1,result);
    }

    @Test
    @Order(2)
    @DisplayName("findById-student1")
    void findById() {
        Optional<Student> studentOptional = studentRepository.findById(connection,"student1");
        log.info("student:{}", studentOptional.get());

        Assertions.assertAll(
                ()->Assertions.assertEquals("student1",studentOptional.get().getId()),
                ()->Assertions.assertEquals("학생1",studentOptional.get().getName()),
                ()->Assertions.assertEquals(Student.GENDER.M,studentOptional.get().getGender())
        );

    }

    @Test
    @Order(3)
    @DisplayName("findById-marco10000")
    void findById_10000(){
        Optional<Student> studentOptional = studentRepository.findById(connection,"student10000");
        Assertions.assertFalse(studentOptional.isPresent());
    }

    @Test
    @Order(4)
    @DisplayName("update : student1")
    void update() {

        Student student = new Student("student1","엔에이치엔아카데미", Student.GENDER.F,30);
        int result = studentRepository.update(connection,student);
        //Assume.assumeFalse(result>0);

        Optional<Student> newStudent = studentRepository.findById(connection,student.getId());


        Assertions.assertAll(
                ()->Assertions.assertEquals("student1",newStudent.get().getId()),
                ()->Assertions.assertEquals("엔에이치엔아카데미",newStudent.get().getName()),
                ()->Assertions.assertEquals(Student.GENDER.F,newStudent.get().getGender()),
                ()->Assertions.assertEquals(30,newStudent.get().getAge())
        );

    }

    @Test
    @Order(5)
    @DisplayName("delete : student1")
    void deleteById() {
        String id="student1";
        int result = studentRepository.deleteById(connection,id);
        Optional<Student> studentDto = studentRepository.findById(connection,id);
        Assertions.assertFalse(studentDto.isPresent());
    }

}