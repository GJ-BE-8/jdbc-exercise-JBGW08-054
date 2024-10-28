package com.nhnacademy.jdbc.club.repository.impl;

import com.nhnacademy.jdbc.club.domain.Club;
import com.nhnacademy.jdbc.club.repository.ClubRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ClubRepositoryImplTest {
    //todo#8 ClubRepositoryImplTest를 실행하고 테스트 코드가 통과할 수 있도록 ClubRepositoryImpl을 수정하세요.
    Connection connection;

    ClubRepository clubRepository;
    @BeforeEach
    void setUp() throws SQLException {
        clubRepository = new ClubRepositoryImpl();
        connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);

        //테스트를 위한 초기화
        clubRepository.deleteByClubId(connection,"c1");
        clubRepository.deleteByClubId(connection,"c2");
        clubRepository.deleteByClubId(connection,"c3");

        Club club1 = new Club("c1","Spring Study");
        Club club2 = new Club("c2","Java Study");

        clubRepository.save(connection,club1);
        clubRepository.save(connection,club2);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
        connection.close();
    }

    @Test
    @DisplayName("조회")
    void findByClubId() {
        Optional<Club> clubOptional = clubRepository.findByClubId(connection,"c1");
        Assertions.assertAll(
                ()->Assertions.assertTrue(clubOptional.isPresent()),
                ()->Assertions.assertEquals(clubOptional.get().getClubId(),"c1"),
                ()->Assertions.assertEquals(clubOptional.get().getClubName(),"Spring Study")
        );
    }

    @Test
    @DisplayName("조회-존재하지 않는 클럽")
    void findByClubId_not_exist_club() {
        Optional<Club> clubOptional = clubRepository.findByClubId(connection,"c3");
        Assertions.assertFalse(clubOptional.isPresent());
    }


    @Test
    @DisplayName("club 등록")
    void save() {
        Club club = new Club("c3","JavaScript Study");
        int result = clubRepository.save(connection,club);

        Assertions.assertAll(
                ()->Assertions.assertEquals(1,result),
                ()->Assertions.assertEquals(clubRepository.findByClubId(connection,"c3").get(),club)
        );
    }

    @Test
    @DisplayName("club 등록 - 중복 아이디 : c1")
    void save_duplicated_club_id(){
        Club club = new Club("c1","Spring Study");
        Throwable throwable = Assertions.assertThrows(RuntimeException.class,()->{
           clubRepository.save(connection,club);
        });
        log.debug("error:{}",throwable.getMessage());
    }

    @Test
    @DisplayName("club 이름 수정")
    void update() {
        Club club = new Club("c1","Spring Study");
        int actual = clubRepository.update(connection, club);
        Assertions.assertEquals(1,actual);
    }

    @Test
    @DisplayName("존재하지 않는 club의 이름 수정")
    void update_not_exist_club_id() {
        Club club = new Club("c3","Spring Study");
        int actual = clubRepository.update(connection, club);
        Assertions.assertEquals(0,actual);
    }

    @Test
    @DisplayName("클럽 삭제")
    void deleteByClubId() {
        String clubId = "c1";
        int actual = clubRepository.deleteByClubId(connection,clubId);
        Assertions.assertAll(
                ()->Assertions.assertEquals(1,actual),
                ()->Assertions.assertEquals(0,clubRepository.countByClubId(connection,clubId))
        );
    }

    @Test
    @DisplayName("count by club id:c1")
    void countByClubId() {
        String clubId = "c1";
        int actual = clubRepository.countByClubId(connection, clubId);
        Assertions.assertEquals(1, actual);
    }

    @Test
    @DisplayName("count by not exist club :c3")
    void countByClubId_not_exist_clubId(){
        String clubId = "c3";
        int actual = clubRepository.countByClubId(connection, clubId);
        Assertions.assertEquals(0, actual);
    }

}