package com.nhnacademy.jdbc.simulation.isolation;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.repository.AccountRepository;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;
import com.nhnacademy.jdbc.bank.service.impl.BankServiceImpl;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class RepeatableReadEx2Main {
    static AccountRepository accountRepository = new AccountRepositoryImpl();
    static BankService bankService = new BankServiceImpl(accountRepository);

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

        log.debug("create new account : 30000");
        long newAccountNumber=30000l;
        Account newAccount = new Account(newAccountNumber,"nhn아카데미30000",10_0000);

        //todo#4 connection1<-- 새로운 계좌 30000을 생성합니다.
        bankService.createAccount(connection1,newAccount);
        connection1.commit();

        //todo#4 accountA 죄회
        accountA = bankService.getAccount(connection1,newAccountNumber);
        try {
            //todo#5 accountB 조회, 서로다른 Transaction, 조회 안됨.
            accountB = bankService.getAccount(connection2, newAccountNumber);
        }catch (Exception e){
            accountB = null;
            log.debug("accountB:{}",e.getMessage());
        }

        //todo#6 accountB는 null
        log.debug("================================");
        log.debug("accountA:{}",accountA);
        log.debug("accountB is null :{}", Objects.isNull(accountB));
        log.debug("================================");

        //todo#7 connection2에서 30000계좌에 5만원 입금, bankService.depositAccount() method 내부에 account 존재하는지 check하는 로직이 있음으로 직접 repository를 통해서 입금 시도.
        log.debug("accountB:5만원 입금");
        accountRepository.deposit(connection2,newAccountNumber,50000l);


        //todo#8 accountB 조회
        accountB = bankService.getAccount(connection2,newAccountNumber);

        //todo#9 connection1에서 insert 된 계좌는 확인할 수 없었지만, update를 통해서 새로운 스냇샵 생성
        log.debug("================================");
        log.debug("accountB {}", accountB);
        log.debug("================================");
        connection2.commit();

        connection1.close();
        connection2.close();
    }

    public static void init() throws SQLException {
        Connection connection = DbUtils.getDataSource().getConnection();
        connection.setAutoCommit(false);
        Account account1 = new Account(10000l,"nhn아카데미-10000",10_0000);
        if(bankService.isExistAccount(connection,account1.getAccountNumber())){
            bankService.dropAccount(connection,account1.getAccountNumber());
        }
        connection.commit();
        connection.close();
        bankService.createAccount(connection,account1);
    }
}
