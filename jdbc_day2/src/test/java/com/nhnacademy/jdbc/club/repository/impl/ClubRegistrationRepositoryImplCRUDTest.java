package com.nhnacademy.jdbc.club.repository.impl;

import com.nhnacademy.jdbc.club.domain.Club;
import com.nhnacademy.jdbc.club.domain.ClubStudent;
import com.nhnacademy.jdbc.club.repository.ClubRegistrationRepository;
import com.nhnacademy.jdbc.club.repository.ClubRepository;
import com.nhnacademy.jdbc.student.domain.Student;
import com.nhnacademy.jdbc.student.repository.StudentRepository;
import com.nhnacademy.jdbc.student.repository.impl.StudentRepositoryImpl;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
class ClubRegistrationRepositoryImplCRUDTest {

    //todo#14 ClubRegistrationRepositoryImplCRUDTest Test Case가 통과할 수 있도록 ClubRegistrationRepositoryImpl 구현합니다.

    StudentRepository studentRepository = new StudentRepositoryImpl();
    ClubRegistrationRepository clubRegistrationRepository = new ClubRegistrationRepositoryImpl();
    ClubRepository clubRepository = new ClubRepositoryImpl();

    Connection connection;
    @BeforeEach
    void setUp() throws SQLException {
        connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);

        //데이터가 존재 한다면 원할한 테스트트 위해ㅔ서 삭제.
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s1","c1");

        clubRepository.deleteByClubId(connection,"C1");
        clubRepository.deleteByClubId(connection,"C2");
        studentRepository.deleteById(connection,"s1");

        // club 등록
        Club club1 = new Club("c1","spring club");
        clubRepository.save(connection, club1);

        Club club2 = new Club("c2","java club");
        clubRepository.save(connection, club2);

        //student 등록
        Student student = new Student("s1","nhn아카데미", Student.GENDER.M,30);
        studentRepository.save(connection,student);

        //학생,클럽 등록
        clubRegistrationRepository.save(connection,student.getId(),club1.getClubId());

    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
    }

    @Test
    @DisplayName("클럽-등록")
    void save() {
        String studentId="s1";
        String clubId="c2";
        int actual = clubRegistrationRepository.save(connection,studentId,clubId);
        Assertions.assertEquals(1,actual);

    }

    @Test
    @DisplayName("클럽-중복등록-제약조건")
    void save_duplicated(){
        Throwable throwable = Assertions.assertThrows(RuntimeException.class,()->{
            clubRegistrationRepository.save(connection,"s1","s2");
        });
        log.debug("error:{}", throwable.getMessage());
    }

    @Test
    void deleteByStudentIdAndClubId() {
        String studentId = "s1";
        String clubId="c1";
        int actual = clubRegistrationRepository.deleteByStudentIdAndClubId(connection,studentId,clubId);
        Assertions.assertEquals(1,actual);
    }

    @Test
    void findClubStudentsByStudentId() throws SQLException {
        String studentId="s1";
        List<ClubStudent> clubStudentList = clubRegistrationRepository.findClubStudentsByStudentId(connection,studentId);
        Assertions.assertEquals(1,clubStudentList.size());
    }

}