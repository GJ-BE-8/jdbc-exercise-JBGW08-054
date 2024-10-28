package com.nhnacademy.jdbc.student.repository.impl;

import com.nhnacademy.jdbc.student.domain.Student;
import com.nhnacademy.jdbc.student.repository.StudentRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class StatementStudentRepository implements StudentRepository {
    private Connection connection = DbUtils.getConnection();

    @Override
    public int save(Student student){
        //todo#1 insert student

        /* PreparedStatement
            SQL 쿼리가 미리 컴파일되고 캐시됩니다.
                -> 동일한 쿼리를 여러 번 실행할 때 성능이 향상
            SQL Injection 방어
                SQL 인젝션: 악의적인 사용자가 애플리케이션의 SQL 쿼리문을 조작하여 데이터베이스에 대한 불법적인 접근을 시도
                SQL 쿼리를 미리 정의할 수 있습니다. 쿼리의 필드에는 물음표(?)로 자리 표시자를 사용
                  setString(), setInt() 등의 메소드를 사용하여 자리 표시자에 실제 값을 바인딩
                    ;파라미터가 쿼리의 일부로 포함되지 않고, 별도로 처리됩니다.
                      데이터베이스는 입력된 파라미터를 쿼리의 코드와 구분하여 안전하게 처리합니다.
                      사용자가 입력한 값을 직접 쿼리 문자열에 포함시키지 않고, setString()이나 setInt() 메소드를 통해 안전하게 값이 전달됩니다.

            배치 실행 지원
                여러 쿼리를 한 번에 실행할 수 있는 배치 실행 기능을 지원
                addBatch() 메소드를 사용하여 여러 쿼리를 추가하고, executeBatch() 메소드를 호출하여 한 번에 실행
         */
        String sql = "INSERT INTO jdbc_students (id, name, gender, age) VALUES (?, ?, ?, ?)";
        int rowsAffected = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender().name()); // ??
            pstmt.setInt(4, student.getAge());

            // SQL 실행
            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return rowsAffected; // 성공적으로 삽입된 행 수 반환
    }

    @Override
    public Optional<Student> findById(String id){
        //todo#2 student 조회
//        return Optional.empty();

        String sql = "SELECT * FROM jdbc_students WHERE id = ?";
        Student student = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id); // 매개변수 바인딩

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 결과가 있을 경우 Student 객체 생성
                    student = new Student(rs.getString("id"),
                            rs.getString("name"),
                            Student.GENDER.valueOf(rs.getString("gender")),
                            rs.getInt("age"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    /*
                        getTimestamp: 날짜와 시간을 모두 포함하며, 나노초 단위까지 정밀도를 제공
                            데이터 타입:
                                TIMESTAMP는 특정 타임존을 기준으로 하고,
                                    '1970-01-01 00:00:01' UTC ~ '2038-01-19 03:14:07' UTC
                                    클라이언트의 시간대에 따라 자동으로 변환
                                    기본값으로 CURRENT_TIMESTAMP를 사용할 수 있 (DATETIME 은 불가)
                                    4바이트
                                DATETIME은 고정된 날짜와 시간을 저장
                                    1000-01-01 00:00:00' ~ '9999-12-31 23:59:59'
                                    8바이트
                        getDate: 날짜 정보만 포함하며, 시간 정보는 포함x
                            시간은 00:00:00으로 설정됩
                            시간 정보를 무시하고 날짜만 반환합니다. 따라서 java.sql.Date 객체는 항상 시간 부분이 00:00:00으로 설정됩니다. 이는 날짜만 필요한 경우를 위해
                            DATE 타입
                                *DATETIME 또는 TIMESTAMP에서 getDate()를 호출하면, 반환되는 값은 java.sql.Date 형식이므로 시간 부분이 00:00:00으로 설정됩니다. 이 경우, 만약 시간도 필요하다면 불필요한 정보를 포함
                        getTime: 시간 정보만 포함하며, 날짜 정보는 포함x
                            날짜는 1970-01-01로 설정됩니다.
                            데이터 타입: TIME
                     */
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        // Optional로 감싸서 반환
        return Optional.ofNullable(student);
    }

    @Override
    public int update(Student student){
        //todo#3 student 수정, name <- 수정합니다.

//        return 0;
        String sql = "UPDATE jdbc_students SET name = ?, gender = ?, age = ? WHERE id = ?";
        try ( PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // 쿼리 파라미터 설정
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getGender().name());
            preparedStatement.setInt(3, student.getAge());
            preparedStatement.setString(4, student.getId());

            // 쿼리 실행
            return preparedStatement.executeUpdate(); // 수정된 행 수 반환

        } catch (SQLException e) {
            log.error(e.getMessage());
            return 0; // 에러 발생 시 0 반환
        }
    }

    @Override
    public int deleteById(String id){
       //todo#4 student 삭제

//        return 0;
        String sql = "DELETE FROM jdbc_students WHERE id = ?";
        int rowsAffected = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id); // 매개변수 바인딩

            // DELETE 쿼리 실행
            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return rowsAffected; // 삭제된 행 수 반환
    }

}
