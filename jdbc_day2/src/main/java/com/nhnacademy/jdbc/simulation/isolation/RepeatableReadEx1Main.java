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
public class RepeatableReadEx1Main {
    static BankService bankService = new BankServiceImpl(new AccountRepositoryImpl());

    public static void main(String[] args) throws SQLException, InterruptedException {
        init();
        Thread.sleep(1000);

        //todo#1 connection1, isolation level : REPEATABLE READ
        Connection connection1 = DbUtils.getDataSource().getConnection();
        connection1.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        connection1.setAutoCommit(false);

        //todo#2 connection2, isolation level : REPEATABLE READ
        Connection connection2 = DbUtils.getDataSource().getConnection();
        connection2.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        connection2.setAutoCommit(false);

        long accountNumber = 10000l;

        //todo#3 동일한 시점에 10000 계좌 조회, accountA, accountB 는 서로 동일한 10000 계좌 입니다. 즉 동일한 데이터차 출력됩니다.
        Account accountA = bankService.getAccount(connection1,accountNumber);
        Account accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");

        //todo#4 connection1에서 10000계좌에 5만원 입금 후 commit을 합니다.
        bankService.depositAccount(connection1,accountNumber,50000l);
        connection1.commit();
        log.debug("accountA <-- 50000원 입금");

        //todo#5 connection1 에서는 50000원이 입금처리되었고(commit 호출로 인해서), connection2에서는 todo#3에서 조회했던 데이터를(스냅샷이 조회됨) 확인할 수 있습니다.
        accountA = bankService.getAccount(connection1,accountNumber);
        accountB = bankService.getAccount(connection2,accountNumber);

        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB:{}",accountB);
        log.debug("================================");

        connection2.commit();

        connection1.close();
        connection2.close();
    }

    public static void init() throws SQLException {
        Connection connection = DbUtils.getDataSource().getConnection();
        Account account1 = new Account(10000l,"nhn아카데미-10000",10_0000);
        if(bankService.isExistAccount(connection,account1.getAccountNumber())){
            bankService.dropAccount(connection,account1.getAccountNumber());
        }
        bankService.createAccount(connection,account1);
    }
}
