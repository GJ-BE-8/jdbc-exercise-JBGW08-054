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
public class SerializableMain {

    /**
     * 1.threadA가 10000 계좌를 조회 합니다. 10초 대기 후 commit
     * 2.threadB는 10000 계좌에 5만원 입급을 시도합니다.
     * 3.threadA가 commit 할 때 까지 대기(10초 대기)
     * 4.threadB가 10000 계좌에 5만원 입급처리
     */

    public static void main(String[] args) throws InterruptedException {

        init();
        Thread.sleep(1000);

        //todo#1 threadA <- Account 조회를 하는 Thread, isolation level = SERIALIZABLE, 10000계좌 조회, 10초후 commit 합니다.
        Thread threadA = new AccountThread(10000,10000);

        //todo#2 threadB <- 10000계좌에 5만원 입금하는 Thread, isolation level = SERIALIZABLE, 1초 대기후 commit 합니다.
        Thread threadB = new AccountDepositThread(10000,1000);

        threadA.setName("Thread-A-조회");
        threadA.start();
        //todo#3 threadA가 먼저 동작할 수 있도록 1초 sleep
        Thread.sleep(1000);
        //todo#4 threadB 시작
        threadB.setName("Thread-B-입금");
        threadB.start();

    }

    public static class AccountThread extends Thread{
        private final BankService bankService;
        private final long accountNumber;
        private final long wait;

        public AccountThread(long accountNumber, long wait){
            this.accountNumber = accountNumber;
            this.wait = wait;
            bankService = new BankServiceImpl(new AccountRepositoryImpl());
        }

        @Override
        public void run() {
            Connection connection = null;

            try {
                connection = DbUtils.getDataSource().getConnection();
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                Account account = bankService.getAccount(connection,accountNumber);

                Thread.sleep(wait);

                connection.commit();
                log.debug("thread-name:{},wait:{},account:{}", this.getName(), wait , account);

            } catch (SQLException | InterruptedException e) {

                throw new RuntimeException(e);
            }finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class AccountDepositThread extends Thread{
        private final BankService bankService;
        private final long accountNumber;
        private final long wait;

        public AccountDepositThread(long accountNumber, long wait){
            this.accountNumber = accountNumber;
            this.wait = wait;
            bankService = new BankServiceImpl(new AccountRepositoryImpl());
        }
        
        @Override
        public void run() {
            Connection connection = null;

            try {
                connection = DbUtils.getDataSource().getConnection();
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                bankService.depositAccount(connection,accountNumber,50000);
                Account account = bankService.getAccount(connection,accountNumber);

                Thread.sleep(wait);
                connection.commit();
                log.debug("thread-name:{},wait:{},account:{}", this.getName(), wait , account);

            } catch (SQLException | InterruptedException e) {

                throw new RuntimeException(e);
            }finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void init()  {
        BankService bankService = new BankServiceImpl(new AccountRepositoryImpl());
        Connection connection = null;
        try {
            connection = DbUtils.getDataSource().getConnection();
            connection.setAutoCommit(false);

            Account account1 = new Account(10000l,"nhn아카데미-10000",10_0000);
            if(bankService.isExistAccount(connection,account1.getAccountNumber())){
                bankService.dropAccount(connection,account1.getAccountNumber());
            }
            bankService.createAccount(connection,account1);

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
