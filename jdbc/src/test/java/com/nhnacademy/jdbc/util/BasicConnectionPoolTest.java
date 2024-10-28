package com.nhnacademy.jdbc.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class BasicConnectionPoolTest {

    static BasicConnectionPool basicConnectionPool;
    static Connection connection1;
    static Connection connection2;
    static Connection connection3;
    static Connection connection4;
    static Connection connection5;

    @BeforeAll
    static void setUp() {
        basicConnectionPool = new BasicConnectionPool(com.mysql.cj.jdbc.Driver.class.getName(),"jdbc:mysql://133.186.241.167:3306/nhn_academy_54","nhn_academy_54","[El3ou_R3Mu_8f1V",5);
    }

    @AfterAll
    static void connectionClose() throws SQLException {
        basicConnectionPool.distory();
    }

    @Test
    @Order(0)
    @DisplayName("Driver not found Exception")
    void init(){
        Assertions.assertThrows(RuntimeException.class,
                ()-> new BasicConnectionPool("org.mariadb.jdbc.Driver","jdbcUrl","userName","password",5)
        );
    }

    @Test
    @Order(1)
    @DisplayName("get connection")
    void getConnection() throws InterruptedException, SQLException {
        log.info("get connection");


        connection1 = basicConnectionPool.getConnection();
        connection2 = basicConnectionPool.getConnection();
        connection3 = basicConnectionPool.getConnection();
        Assertions.assertAll(
                ()->Assertions.assertTrue(connection1.isValid(1000)),
                ()->Assertions.assertTrue(connection2.isValid(1000)),
                ()->Assertions.assertTrue(connection3.isValid(1000)),
                ()->Assertions.assertEquals(basicConnectionPool.getUsedConnectionSize(),3)
        );
    }

    @Disabled
    @Test
    @Order(2)
    @DisplayName("empty connection-pool")
    void getConnection_empty() throws InterruptedException, SQLException {
        System.out.println("check point0");
        log.debug("check point0");
        connection4 = basicConnectionPool.getConnection();
        connection5 = basicConnectionPool.getConnection();
        System.out.println("check point1");
        Connection connection6 = basicConnectionPool.getConnection();
        System.out.println("check point2");
        Assertions.assertAll(
                ()->Assertions.assertEquals(basicConnectionPool.getUsedConnectionSize(),5)
        );

    }

    @Test
    @Order(3)
    @DisplayName("release connection")
    void releaseConnection() {
        basicConnectionPool.releaseConnection(connection1);
        basicConnectionPool.releaseConnection(connection2);
        basicConnectionPool.releaseConnection(connection3);

        Assertions.assertEquals(basicConnectionPool.getUsedConnectionSize(),0);
    }
}