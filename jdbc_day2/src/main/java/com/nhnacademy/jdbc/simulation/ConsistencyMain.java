package com.nhnacademy.jdbc.simulation;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;
import com.nhnacademy.jdbc.bank.service.impl.BankServiceImpl;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class ConsistencyMain {
    static BankService bankService = new BankServiceImpl(new AccountRepositoryImpl());

    public static void main(String[] args) throws SQLException {


        Connection connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);
        init(connection);

        try {

            Account account1 = new Account(8000, "nhn아카데미", 10_0000);
            //todo#1 계좌번호가 8000인 account1 생성
            bankService.createAccount(connection, account1);
            log.debug("account1->8000계좌 생성");

            //todo#2 계좌번호가 8000인 account2생성
            Account account2 = new Account(8000, "nhn아카데미", 10_0000);
            bankService.createAccount(connection, account2);
            log.debug("account2->8000계좌 생성 시도");

            connection.commit();


        }catch (Exception e){
            log.debug("error:{}", e.getMessage());
            log.debug("account1,8000 계좌가 생성되어 있음으로 rollback 처리, account1, account2 동일한 Transaction에 속함으로 모두 rollback");
            
            //todo#3 account_number(계좌번호) primary key 제약조건 위배. -> rollback
            connection.rollback();
        }

        connection.close();
    }

    public static void init(Connection connection){
        //8000계좌가 있다면 미리 삭제함.
        try {
            if( bankService.isExistAccount(connection,8000l)) {
                bankService.dropAccount(connection, 8000l);
            }
        }catch (Exception e){
            log.debug("init:{}",e.getMessage());
        }
    }

}
