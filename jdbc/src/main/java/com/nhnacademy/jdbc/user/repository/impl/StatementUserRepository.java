package com.nhnacademy.jdbc.user.repository.impl;

import com.nhnacademy.jdbc.user.domain.User;
import com.nhnacademy.jdbc.user.repository.UserRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
public class StatementUserRepository implements UserRepository {

    @Override
    public Optional<User> findByUserIdAndUserPassword(String userId, String userPassword) {
        String sql = String.format("select user_id, user_name, user_password from jdbc_users where user_id='%s' and user_password='%s' ", userId, userPassword);
        log.debug("sql:{}",sql);
        try(Connection connection = DbUtils.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
        ) {
            if(rs.next()){
                return Optional.of(
                        new User(rs.getString("user_id"),rs.getString("user_name"),rs.getString("user_password"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String userId) {
        String sql = String.format("select user_id, user_name, user_password from jdbc_users where user_id='%s'", userId);

        try(Connection connection = DbUtils.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
        ) {
            if(rs.next()){
                return Optional.of(
                        new User(rs.getString("user_id"),rs.getString("user_name"),rs.getString("user_password"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public int save(User user) {
        String sql = String.format("insert into jdbc_users (user_id, user_name, user_password) values ('%s','%s','%s')",
                user.getUserId(),
                user.getUserName(),
                user.getUserPassword()
        );

        try(Connection connection = DbUtils.getConnection();
            Statement statement = connection.createStatement();
        ){
            int result = statement.executeUpdate(sql);
            log.debug("save-result:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateUserPasswordByUserId(String userId, String userPassword) {
        String sql = String.format("update jdbc_users set user_password='%s' where user_id='%s'",
                    userPassword,
                    userId
                );
        log.debug("sql:{}",sql);
        try(Connection connection = DbUtils.getConnection();
            Statement statement = connection.createStatement();
        ){
            int result = statement.executeUpdate(sql);
            log.debug("updateUserPasswordByUserId : {}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = String.format("delete from jdbc_users where user_id='%s'", userId);
        try(Connection connection = DbUtils.getConnection();
            Statement statement = connection.createStatement();
        ){
            int result = statement.executeUpdate(sql);
            log.debug("result:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
