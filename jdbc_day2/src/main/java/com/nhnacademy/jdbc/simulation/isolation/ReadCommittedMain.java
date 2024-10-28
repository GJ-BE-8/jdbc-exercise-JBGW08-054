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
public class ReadCommittedMain {
    static final BankService bankService = new BankServiceImpl(new AccountRepositoryImpl());
    public static void main(String[] args) throws SQLException, InterruptedException {

        init();
        Thread.sleep(1000);

        long accountNumber=10000l;
        //todo#1 connection1,connection2  isolation level : READ_COMMITED로 설정합니다.
        Connection connection1 = DbUtils.getDataSource().getConnection();
        connection1.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        connection1.setAutoCommit(false);

        Connection connection2 = DbUtils.getDataSource().getConnection();
        connection2.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        connection2.setAutoCommit(false);


        //todo#2 10000 계좌 조회
        Account accountA = bankService.getAccount(connection1,accountNumber);
        Account accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");

        //todo#3 accountA 예금 : 5만원, commit은 하지 않습니다.
        bankService.depositAccount(connection1,accountNumber,5_0000l);

        //todo#4 accountA, accountB 10000계좌의 잔액조회, accountA:15만원 accountB는 10만원을 유지합니다.
        accountA = bankService.getAccount(connection1,accountNumber);
        accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");

        //todo#5 accountA 예금 : 5만원, commit은 하지 않습니다.
        bankService.depositAccount(connection1,accountNumber,5_0000l);

        //todo#4 accountA, accountB 10000계좌의 잔액조회, accountA:20만원 accountB는 10만원을 유지합니다.
        accountA = bankService.getAccount(connection1,accountNumber);
        accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");

        //todo accountB는 잔액을 10만원을 출력 합니다. 즉 DIRTY READ를 방지합니다.
        // accountA 즉 connection1의 transaction 작업이 완료안됨 -> accountB 즉 connection2의 transaction에서는 connection1 Transaction에서 변경된 값을 읽을 수 없습니다.

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
