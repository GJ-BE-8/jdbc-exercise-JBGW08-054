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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class ClubRegistrationRepositoryImplTest {
    //todo#28 - ClubRegistrationRepositoryImplTest 실항하여 Test Case가 통과할 수 있도록 ClubRegistrationRepositoryImpl 구현합니다.
    static Connection connection;
    static ClubRepository clubRepository = new ClubRepositoryImpl();
    static StudentRepository studentRepository = new StudentRepositoryImpl();
    static ClubRegistrationRepository clubRegistrationRepository = new ClubRegistrationRepositoryImpl();

    @BeforeAll
    static void setUp() throws SQLException {

        connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);

        //초기화 테스트를 위해서 데이터가 남아 있다면 삭제
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s1","c1");
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s1","c2");
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s1","c3");
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s2","c1");
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s3","c2");
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s4","c2");
        clubRegistrationRepository.deleteByStudentIdAndClubId(connection,"s5","c3");

        for(int i=0; i<10; i++){
            String studentId="s"+i;
            String clubId="c"+i;
            clubRepository.deleteByClubId(connection,clubId);
            studentRepository.deleteById(connection,studentId);
        }

        // club 등록
        Club club1 = new Club("c1","Spring club");
        clubRepository.save(connection, club1);

        Club club2 = new Club("c2","GoLang club");
        clubRepository.save(connection, club2);

        Club club3 = new Club("c3","Python club");
        clubRepository.save(connection, club3);

        Club club4 = new Club("c4","c# club");
        clubRepository.save(connection, club4);

        Club club5 = new Club("c5","php club");
        clubRepository.save(connection, club5);

        Iterator<Integer> intIterator = new Random().ints(20,30).iterator();

        //student테이브 비우기
        studentRepository.deleteAll(connection);

        //학생 10명 생성
        for(int i=0; i<10; i++){
            int age = intIterator.next();
            Student student = new Student("s"+i,"학생"+i, i%2==1 ? Student.GENDER.M : Student.GENDER.F , age);
            studentRepository.save(connection,student);
        }

        //s1->c1
        clubRegistrationRepository.save(connection,"s1","c1");
        //s1->c2
        clubRegistrationRepository.save(connection,"s1","c2");
        //s1->c3
        clubRegistrationRepository.save(connection,"s1","c3");

        //s2->c1
        clubRegistrationRepository.save(connection,"s2","c1");
        //s3->c2
        clubRegistrationRepository.save(connection,"s3","c2");
        //s4->c2
        clubRegistrationRepository.save(connection,"s4","c2");
        //s5->c3
        clubRegistrationRepository.save(connection,"s5","c3");

    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.rollback();
        connection.close();
    }

    @Test
    @Order(1)
    @DisplayName("inner join")
    void findClubStudents() {

        /*
         select
            a.id as student_id,
            a.name as student_name,
            c.club_id,
            c.club_name
         from jdbc_students a
            inner join jdbc_club_registrations b on a.id=b.student_id
            inner join jdbc_club c on b.club_id=c.club_id
         order by a.id asc, b.club_id asc

         */

        List<ClubStudent> expected = List.of(
                new ClubStudent("s1","학생1","c1","Spring club"),
                new ClubStudent("s1","학생1","c2","GoLang club"),
                new ClubStudent("s1","학생1","c3","Python club"),
                new ClubStudent("s2","학생2","c1","Spring club"),
                new ClubStudent("s3","학생3","c2","GoLang club"),
                new ClubStudent("s4","학생4","c2","GoLang club"),
                new ClubStudent("s5","학생5","c3","Python club")
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(2)
    @DisplayName("left join")
    void findClubStudents_left_join() {
        /*
            select
                a.id as student_id,
                a.name as student_name,
                c.club_id,
                c.club_name
            from jdbc_students a
                left join jdbc_club_registrations b on a.id=b.student_id
                left join jdbc_club c on b.club_id=c.club_id
                order by a.id asc, b.club_id asc
        */


        List<ClubStudent> expected = List.of(
                new ClubStudent("s0","학생0",null,null),
                new ClubStudent("s1","학생1","c1","Spring club"),
                new ClubStudent("s1","학생1","c2","GoLang club"),
                new ClubStudent("s1","학생1","c3","Python club"),
                new ClubStudent("s2","학생2","c1","Spring club"),
                new ClubStudent("s3","학생3","c2","GoLang club"),
                new ClubStudent("s4","학생4","c2","GoLang club"),
                new ClubStudent("s5","학생5","c3","Python club"),
                new ClubStudent("s6","학생6",null,null),
                new ClubStudent("s7","학생7",null,null),
                new ClubStudent("s8","학생8",null,null),
                new ClubStudent("s9","학생9",null,null)
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents_left_join(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);

    }

    @Test
    @Order(3)
    @DisplayName("right join")
    void findClubStudents_right_join() {

        /*
            select
                a.id as student_id,
                a.name as student_name,
                c.club_id,
                c.club_name
            from jdbc_students a
                right join jdbc_club_registrations b on a.id=b.student_id
                right join jdbc_club c on b.club_id=c.club_id
                order by c.club_id asc,a.id asc
        */


        List<ClubStudent> expected = List.of(
                new ClubStudent("s1","학생1","c1","Spring club"),
                new ClubStudent("s2","학생2","c1","Spring club"),
                new ClubStudent("s1","학생1","c2","GoLang club"),
                new ClubStudent("s3","학생3","c2","GoLang club"),
                new ClubStudent("s4","학생4","c2","GoLang club"),
                new ClubStudent("s1","학생1","c3","Python club"),
                new ClubStudent("s5","학생5","c3","Python club"),
                new ClubStudent(null,null,"c4","c# club"),
                new ClubStudent(null,null,"c5","php club")
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents_right_join(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);

    }

    @Test
    @Order(4)
    @DisplayName("full join")
    void findClubStudents_full_join() {

        /* 정렬은 하지 않습니다. mysql full join을 지원하지 않습니다. 대신 left join + right join 결과를 union을 통해서 중복 제거한 합집합을 반환합니다.
        select
            a.id as student_id,
            a.name as student_name,
            c.club_id,
            c.club_name
        from jdbc_students a
        left join jdbc_club_registrations b on a.id=b.student_id
        left join jdbc_club c on b.club_id=c.club_id

        union

        select
            a.id as student_id,
            a.name as student_name,
            c.club_id,
            c.club_name
        from
            jdbc_club c
            right join jdbc_club_registrations b on c.club_id=b.club_id
            right join jdbc_students a on a.id=b.student_id

        */

        List<ClubStudent> expected = List.of(
                new ClubStudent("s0","학생0",null,null),
                new ClubStudent("s1","학생1","c1","Spring club"),
                new ClubStudent("s1","학생1","c2","GoLang club"),
                new ClubStudent("s1","학생1","c3","Python club"),
                new ClubStudent("s2","학생2","c1","Spring club"),
                new ClubStudent("s3","학생3","c2","GoLang club"),
                new ClubStudent("s4","학생4","c2","GoLang club"),
                new ClubStudent("s5","학생5","c3","Python club"),
                new ClubStudent("s6","학생6",null,null),
                new ClubStudent("s7","학생7",null,null),
                new ClubStudent("s8","학생8",null,null),
                new ClubStudent("s9","학생9",null,null),
                new ClubStudent(null,null,"c4","c# club"),
                new ClubStudent(null,null,"c5","php club")
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents_full_join(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(5)
    @DisplayName("left excluding join")
    void findClubStudents_left_excluding_join() {
        /*
            select
                a.id as student_id,
                a.name as student_name,
                c.club_id,
                c.club_name
            from jdbc_students a
                left join jdbc_club_registrations b on a.id=b.student_id
                left join jdbc_club c on b.club_id=c.club_id
            where c.club_id is null order by a.id asc
        */

        List<ClubStudent> expected = List.of(
                new ClubStudent("s0","학생0",null,null),
                new ClubStudent("s6","학생6",null,null),
                new ClubStudent("s7","학생7",null,null),
                new ClubStudent("s8","학생8",null,null),
                new ClubStudent("s9","학생9",null,null)
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents_left_excluding_join(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(6)
    @DisplayName("right excluding join")
    void findClubStudents_right_excluding_join() {

        /*
            select
                a.id as student_id,
                a.name as student_name,
                c.club_id,
                c.club_name
            from jdbc_students a
                right join jdbc_club_registrations b on a.id=b.student_id
                right join jdbc_club c on b.club_id=c.club_id
            where a.id is null order by b.club_id asc
        */

        List<ClubStudent> expected = List.of(
                new ClubStudent(null,null,"c4","c# club"),
                new ClubStudent(null,null,"c5","php club")
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents_right_excluding_join(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);

    }

    @Test
    @Order(7)
    @DisplayName("outher excluding join")
    void findClubStudents_outher_excluding_join() {

        /* mysql은 full outher join을 제공하지 않음 ,  left excluding join + right excluding join union해서 구할수 있음

            select
                a.id as student_id,
                a.name as student_name,
                c.club_id,
                c.club_name
            from jdbc_students a
            left join jdbc_club_registrations b on a.id=b.student_id
            left join jdbc_club c on b.club_id=c.club_id
            where c.club_id is null

            union

            select
                a.id as student_id,
                a.name as student_name,
                c.club_id,
                c.club_name
            from jdbc_students a
            right join jdbc_club_registrations b on a.id=b.student_id
            right join jdbc_club c on b.club_id=c.club_id
            where a.id is null

        */

        List<ClubStudent> expected = List.of(
                new ClubStudent("s0","학생0",null,null),
                new ClubStudent("s6","학생6",null,null),
                new ClubStudent("s7","학생7",null,null),
                new ClubStudent("s8","학생8",null,null),
                new ClubStudent("s9","학생9",null,null),
                new ClubStudent(null,null,"c4","c# club"),
                new ClubStudent(null,null,"c5","php club")
        );

        List<ClubStudent> actual = clubRegistrationRepository.findClubStudents_outher_excluding_join(connection);

        for(ClubStudent clubStudent : actual){
            log.debug("clubStudent:{}",clubStudent);
        }

        Assertions.assertEquals(expected,actual);

    }
}