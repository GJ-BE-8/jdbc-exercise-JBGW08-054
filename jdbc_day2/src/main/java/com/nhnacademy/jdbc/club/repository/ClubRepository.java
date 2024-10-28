package com.nhnacademy.jdbc.club.repository;

import com.nhnacademy.jdbc.club.domain.Club;

import java.sql.Connection;
import java.util.Optional;

public interface ClubRepository {

    //조회
    Optional<Club> findByClubId(Connection connection, String clubId);
    //저장
    int save(Connection connection, Club club);
    //수정
    int update(Connection connection, Club club);
    //삭제
    int deleteByClubId(Connection connection, String clubId);

    int countByClubId(Connection connection, String clubId);
    
}
