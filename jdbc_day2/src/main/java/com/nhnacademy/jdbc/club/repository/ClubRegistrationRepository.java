package com.nhnacademy.jdbc.club.repository;

import com.nhnacademy.jdbc.club.domain.ClubStudent;
import java.sql.Connection;
import java.util.List;

public interface ClubRegistrationRepository {

    //학생-클럽 등록
    int save(Connection connection, String studentId, String clubId);

    //학생-클럽 삭제
    int deleteByStudentIdAndClubId(Connection connection, String studentId, String clubId);

    //특정 학생이 가입한 클럽 조회
    List<ClubStudent> findClubStudentsByStudentId(Connection connection, String studentId);

    //학생-클럽 리스트 조회 inner Join
    List<ClubStudent> findClubStudents(Connection connection);

    //학생기준 클럽등록 현황
    List<ClubStudent> findClubStudents_left_join(Connection connection);

    //클럽기준 학생등록 현황
    List<ClubStudent> findClubStudents_right_join(Connection connection);

    //학생 + 클럽 모든 현황 (합집합)
    List<ClubStudent> findClubStudents_full_join(Connection connection);

    //클럽에 등록하지 않은 학생현황
    List<ClubStudent> findClubStudents_left_excluding_join(Connection connection);

    //학생이 등록하지 않은 클럽현황
    List<ClubStudent> findClubStudents_right_excluding_join(Connection connection);

    //아무도 등록하지 않은 학생과, 클럽현황
    List<ClubStudent> findClubStudents_outher_excluding_join(Connection connection);

}
