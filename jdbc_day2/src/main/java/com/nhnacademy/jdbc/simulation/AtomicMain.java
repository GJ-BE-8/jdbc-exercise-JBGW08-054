package com.nhnacademy.jdbc.simulation;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;
import com.nhnacademy.jdbc.bank.service.impl.BankServiceImpl;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class AtomicMain {

    public static void main(String[] args) throws SQLException {
        BankService bankService = new BankServiceImpl(new AccountRepositoryImpl());

        Connection connection = DbUtils.getDataSource().getConnection();

        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        //todo1 - 계좌생성 : 8000
        bankService.createAccount(connection,new Account(8000l,"nhn아카데미-8000",10_0000l ));

        try{
            //todo2 - 8000계좌에서 -> 5_0000인출
            bankService.withdrawAccount(connection,8000l,5_0000);

            //todo3 - 9000 계좌에 -> 5_0000입급
            bankService.depositAccount(connection,9000l,5_0000);
            //9000 계좌는 존재하지 않음으로 AccountNotFoundException 예외발생

            connection.commit();
        }catch (Exception e){
            log.debug("withdraw:{}",e.getMessage());
            //todo4 - rollback
            connection.rollback();
        }
        
        connection.close();
    }

}
