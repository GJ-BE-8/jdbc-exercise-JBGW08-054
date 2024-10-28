package com.nhnacademy.jdbc.user.repository.impl;

import com.nhnacademy.jdbc.user.domain.User;
import com.nhnacademy.jdbc.user.repository.UserRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class PreparedStatementUserRepository implements UserRepository {
    @Override
    public Optional<User> findByUserIdAndUserPassword(String userId, String userPassword) {

        String sql = "select user_id, user_name, user_password from jdbc_users where user_id=? and user_password=?";
        log.debug("sql:{}",sql);
        ResultSet rs = null;
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1,userId);
            statement.setString(2,userPassword);
            rs = statement.executeQuery();

            if(rs.next()){
                return Optional.of(
                        new User(rs.getString("user_id"),rs.getString("user_name"),rs.getString("user_password"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String userId) {
        String sql = "select user_id, user_name, user_password from jdbc_users where user_id=?";
        ResultSet rs = null;
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1,userId);
            rs = statement.executeQuery();
            if(rs.next()){
                return Optional.of(
                        new User(rs.getString("user_id"),rs.getString("user_name"),rs.getString("user_password"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public int save(User user) {
        String sql = "insert into jdbc_users (user_id, user_name, user_password) values (?,?,?)";

        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1,user.getUserId());
            statement.setString(2, user.getUserName());
            statement.setString(3,user.getUserPassword());

            int result = statement.executeUpdate();
            log.debug("save-result:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateUserPasswordByUserId(String userId, String userPassword) {
        String sql = "update jdbc_users set user_password=? where user_id=?";
        log.debug("sql:{}",sql);
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1,userPassword);
            statement.setString(2,userId);
            int result = statement.executeUpdate();
            log.debug("updateUserPasswordByUserId : {}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteByUserId(String userId) {
        String sql = "delete from jdbc_users where user_id=?";
        log.debug("sql:{}",sql);
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1,userId);
            int result = statement.executeUpdate();
            log.debug("result:{}", result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
