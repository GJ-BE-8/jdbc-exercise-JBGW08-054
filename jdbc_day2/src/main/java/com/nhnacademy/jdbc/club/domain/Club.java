package com.nhnacademy.jdbc.club.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Club {
    //todo#1 Club entity는 이아디, 이름, 생성일자 attribute를 가지고 있습니다.
    private final String clubId;
    private final String clubName;
    private final LocalDateTime clubCreatedAt;

    public Club(String clubId, String clubName) {
        this(clubId,clubName,LocalDateTime.now());
    }

    public Club(String clubId, String clubName, LocalDateTime clubCreatedAt) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.clubCreatedAt = clubCreatedAt;
    }

    public String getClubId() {
        return clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public LocalDateTime getClubCreatedAt() {
        return clubCreatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return Objects.equals(clubId, club.clubId) && Objects.equals(clubName, club.clubName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clubId, clubName);
    }
}
