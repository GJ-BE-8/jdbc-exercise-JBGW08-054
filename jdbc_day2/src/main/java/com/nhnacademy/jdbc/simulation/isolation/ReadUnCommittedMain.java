package com.nhnacademy.jdbc.simulation.isolation;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;
import com.nhnacademy.jdbc.bank.service.impl.BankServiceImpl;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class ReadUnCommittedMain {
    static BankService bankService = new BankServiceImpl(new AccountRepositoryImpl());

    public static void main(String[] args) throws SQLException, InterruptedException {
        init();
        Thread.sleep(1000);

        //todo#1 connection1,connection2  isolation level : READ_UNCOMMITED로 설정합니다.
        Connection connection1 = DbUtils.getDataSource().getConnection();
        connection1.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        connection1.setAutoCommit(false);

        Connection connection2 = DbUtils.getDataSource().getConnection();
        connection2.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        connection2.setAutoCommit(false);

        //todo#2 10000인 계좌 connection1,connection2 각각 조회
        long accountNumber=10000l;
        Account accountA = bankService.getAccount(connection1,accountNumber);
        Account accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");


        //todo#3 accountA 5만원 입금, commit 하지 않습니다.
        bankService.depositAccount(connection1,accountNumber,5_0000);


        //todo#4 dirty read 발생, connection1 commit 되지 않는 상태에서 connection2에서 아직 commit되지 않는 데이터를 읽을 수 있음

        accountA = bankService.getAccount(connection1,accountNumber);
        accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");

        connection1.commit();
        connection2.commit();
        connection1.close();
        connection2.close();


    }

    static void init() throws SQLException {
        Connection connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);
        Account account = new Account(10000l,"nhn아카데미-10000",10_0000);

        if(bankService.isExistAccount(connection,account.getAccountNumber())){
            bankService.dropAccount(connection,account.getAccountNumber());
        }
        bankService.createAccount(connection,account);
        connection.commit();
    }

}
