package io.nayasis.sample.registrylock.redis.sample;

import io.nayasis.sample.registrylock.redis.jpa.entity.AccountEntity;
import io.nayasis.sample.registrylock.redis.sample.vo.ReqTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private AccountService service;

    @PostMapping( "/account" )
    public AccountEntity create() {
        return service.createWallet();
    }

    @GetMapping( "/account/{accountId}")
    public AccountEntity get( @PathVariable Long accountId ) {
        return service.get( accountId );
    }

    @PutMapping( "/account/{accountId}/deposit/{amount}" )
    public AccountEntity deposite( @PathVariable Long accountId, @PathVariable BigDecimal amount ) {
        isBiggerThanZero( amount );
        return service.deposite( accountId, amount );
    }

    @PutMapping( "/account/{accountId}/withdraw/{amount}" )
    public AccountEntity withdraw( @PathVariable Long accountId, @PathVariable BigDecimal amount ) {
        isBiggerThanZero( amount );
        return service.withdraw( accountId, amount );
    }

    @GetMapping( "/account/all" )
    public List<AccountEntity> getAll() {
        return service.getAll();
    }

    @PutMapping( "/account/transfer" )
    public void transfer( @RequestBody ReqTransfer request ) {
        isBiggerThanZero( request.getAmount() );
        service.transfer( request );
    }

    private void isBiggerThanZero( BigDecimal amount ) {
        Assert.isTrue( amount != null && amount.compareTo( BigDecimal.ZERO ) > 0, "amount must be greater than 0" );
    }

}
