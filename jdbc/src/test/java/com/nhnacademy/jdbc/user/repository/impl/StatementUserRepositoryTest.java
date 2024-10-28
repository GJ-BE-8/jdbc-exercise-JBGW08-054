package com.nhnacademy.jdbc.user.repository.impl;

import com.nhnacademy.jdbc.user.domain.User;
import com.nhnacademy.jdbc.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class StatementUserRepositoryTest {

    static UserRepository userRepository;

    @BeforeAll
    static void setUp() {
        userRepository = new StatementUserRepository();

        //todo 테스트가 시작될 때 user1 ~ user10 까지 mysql 데이테이스에 등록하세요.
        //user_name = user1 ~ user10, user_name=유저1~유저10, user_password=nhnacademy
        //ex) user1이 존재하면 insert query를 실행하지 않습니다.

        for(int i=1; i<=10; i++){
            String userId="user"+i;
            String userName="유저"+i;
            String userPassword="nhnacademy";
            User newUser = new User(userId,userName,userPassword);
            Optional<User> userOptional =  userRepository.findById(userId);
            if(!userOptional.isPresent()){
                userRepository.save(newUser);
            }
        }
        userRepository.deleteByUserId("user100");
    }

    @Test
    @Order(1)
    @DisplayName("find user by id and password")
    void findByUserIdAndUserPassword() {
        String id="user1";
        String password="nhnacademy";
        //todo
        Optional<User> userOptional = userRepository.findByUserIdAndUserPassword(id,password);
        Assertions.assertAll(
                ()->Assertions.assertTrue(userOptional.isPresent()),
                ()->Assertions.assertEquals(userOptional.get().getUserId(),id),
                ()->Assertions.assertEquals(userOptional.get().getUserPassword(),password)
        );
    }

    @Test
    @Order(2)
    @DisplayName("login - sql injection")
    void findByUserIdAndUserPAssword_sql_injection(){
        String id="user1";
        String password="' or '1'='1";

        //todo
        Optional<User> userOptional = userRepository.findByUserIdAndUserPassword(id,password);

        log.debug("userId:{}",userOptional.get().getUserId());
        log.debug("userName:{}",userOptional.get().getUserName());
        log.debug("userPassword:{}",userOptional.get().getUserPassword());

        Assertions.assertTrue(userOptional.isPresent());
    }

    @Test
    @Order(3)
    @DisplayName("find user by id")
    void findById() {
        String id="user1";
        //todo
        Optional<User> userOptional = userRepository.findById(id);
        Assertions.assertAll(
                ()->Assertions.assertTrue(userOptional.isPresent()),
                ()->Assertions.assertEquals(userOptional.get().getUserId(),id)
        );
    }

    @Test
    @Order(4)
    void save() {
        //todo
        User newUser = new User("user100","유저100","nhnacademy");
        int result = userRepository.save(newUser);

        Assertions.assertAll(
                ()->Assertions.assertEquals(1,result),
                ()->{
                    Optional<User> userOptional = userRepository.findById(newUser.getUserId());
                    Assertions.assertAll(
                            ()->Assertions.assertTrue(userOptional.isPresent()),
                            ()->Assertions.assertEquals(userOptional.get().getUserId(),newUser.getUserId()),
                            ()->Assertions.assertEquals(userOptional.get().getUserName(),newUser.getUserName()),
                            ()->Assertions.assertEquals(userOptional.get().getUserPassword(),newUser.getUserPassword())
                    );
                }
        );
    }

    @Test
    @Order(5)
    @DisplayName("change password , user100")
    void updateUserPasswordByUserId() {
        String id="user100";
        String newPassword="12345";
        //todo

        int result = userRepository.updateUserPasswordByUserId(id,newPassword);
        Assertions.assertEquals(1,result);

        Optional<User> userOptional = userRepository.findById(id);
        Assertions.assertAll(
                ()->Assertions.assertTrue(userOptional.isPresent()),
                ()->Assertions.assertEquals(userOptional.get().getUserId(),id),
                ()->Assertions.assertEquals(userOptional.get().getUserPassword(),newPassword)
        );
    }

    @Test
    @Order(6)
    @DisplayName("delete by userid : user100")
    void deleteByUserId() {
        //todo delete user :  user100
        String id = "user100";
        int result = userRepository.deleteByUserId(id);
        Assertions.assertAll(
            ()->Assertions.assertEquals(1,result),
            ()->Assertions.assertFalse(userRepository.findById(id).isPresent())
        );
    }
}