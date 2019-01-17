package io.nayasis.sample.registrylock.redis.sample;

import io.nayasis.sample.registrylock.redis.jpa.entity.AccountEntity;
import io.nayasis.sample.registrylock.redis.sample.vo.ReqDeposit;
import io.nayasis.sample.registrylock.redis.sample.vo.ReqTransfer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = RANDOM_PORT )
public class AccountControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final BigDecimal AMOUNT = new BigDecimal( 10 );

    @Test
    public void integration() {

        AccountEntity account1 = createAccount();
        AccountEntity account2 = createAccount();

        assertNotEquals( account1.getAccountId(), account2.getAccountId() );

        System.out.println( account1 );
        System.out.println( account2 );

        AccountEntity updateAccount1 = deposite( account1.getAccountId(), AMOUNT );
        AccountEntity updateAccount2 = deposite( account2.getAccountId(), AMOUNT );

        assertTrue( account1.getBalance().add( AMOUNT ).equals(updateAccount1.getBalance()) );
        assertTrue( account2.getBalance().add( AMOUNT ).equals(updateAccount2.getBalance()) );

        account1 = updateAccount1;
        account2 = updateAccount2;

        transfer( account1.getAccountId(), account2.getAccountId(), AMOUNT );

        getAccount( account1.getAccountId() ).getBalance().equals( account1.getBalance().subtract( AMOUNT ) );
        getAccount( account2.getAccountId() ).getBalance().equals( account2.getBalance().add( AMOUNT ) );

    }

    @Test
    public void boundaryCondition() {

        AccountEntity account = createAccount();

        try {
            deposite( account.getAccountId(), AMOUNT.negate() );
            throw new IllegalStateException( "amount negative checker not working." );
        } catch ( AssertionError e ) {}

        try {
            withdraw( account.getAccountId(), AMOUNT.negate() );
            throw new IllegalStateException( "amount negative checker not working." );
        } catch ( AssertionError e ) {}

        try {
            getAccount( -1L );
            throw new IllegalStateException( "empty account checker not working." );
        } catch ( AssertionError e ) {}

        try {
            withdraw( account.getAccountId(), account.getBalance().add( AMOUNT ) );
            throw new IllegalStateException( "insufficient balance checker not working." );
        } catch ( AssertionError e ) {}

    }

    private AccountEntity createAccount() {

        ResponseEntity<AccountEntity> res = restTemplate.postForEntity( "/account", null, AccountEntity.class );

        assertSame( res.getStatusCode(), HttpStatus.OK );

        AccountEntity account = res.getBody();

        assertTrue( account != null );
        assertTrue( account.getAccountId() != null );

        return account;

    }

    private void transfer( Long from, Long to, BigDecimal amount ) {

        ReqTransfer param = new ReqTransfer();
        param.setFrom( from );
        param.setTo( to );
        param.setAmount( amount );

        HttpEntity<ReqTransfer> request = new HttpEntity<>( param, null );

        ResponseEntity<Void> res = restTemplate.exchange( "/account/transfer", HttpMethod.PUT, request, Void.class );

        assertSame( res.getStatusCode(), HttpStatus.OK );

    }

    private AccountEntity getAccount( Long accountId ) {
        ResponseEntity<AccountEntity> res = restTemplate.getForEntity( "/account/" + accountId, AccountEntity.class );
        assertSame( res.getStatusCode(), HttpStatus.OK );
        return res.getBody();
    }

    private AccountEntity deposite( Long accountId, BigDecimal amount ) {

        ReqDeposit param = new ReqDeposit();
        param.setId( accountId );
        param.setAmount( amount );

        HttpEntity<ReqDeposit> request = new HttpEntity<>( param, null );

        ResponseEntity<AccountEntity> res = restTemplate.exchange(
            String.format("/account/%s/deposit/%s", accountId, amount ),
            HttpMethod.PUT, request, AccountEntity.class );

        assertSame( res.getStatusCode(), HttpStatus.OK );

        return res.getBody();

    }

    private AccountEntity withdraw( Long accountId, BigDecimal amount ) {

        ReqDeposit param = new ReqDeposit();
        param.setId( accountId );
        param.setAmount( amount );

        HttpEntity<ReqDeposit> request = new HttpEntity<>( param, null );

        ResponseEntity<AccountEntity> res = restTemplate.exchange(
            String.format("/account/%s/withdraw/%s", accountId, amount ),
            HttpMethod.PUT, request, AccountEntity.class );

        assertSame( res.getStatusCode(), HttpStatus.OK );

        return res.getBody();

    }

}