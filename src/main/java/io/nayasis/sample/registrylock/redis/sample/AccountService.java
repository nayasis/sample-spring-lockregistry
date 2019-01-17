package io.nayasis.sample.registrylock.redis.sample;

import io.nayasis.sample.registrylock.redis.core.redis.Locker;
import io.nayasis.sample.registrylock.redis.jpa.entity.AccountEntity;
import io.nayasis.sample.registrylock.redis.jpa.repository.AccountRepository;
import io.nayasis.sample.registrylock.redis.sample.vo.ReqTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Value( "${service.initial.balance:0}" )
    private BigDecimal INITIAL_BALANCE;

    @Autowired
    private AccountRepository repository;

    @Autowired
    private Locker balanceLocker;

    @Transactional
    public AccountEntity createWallet() {

        AccountEntity entity = new AccountEntity();
        entity.setBalance( INITIAL_BALANCE );

        repository.save( entity );

        return entity;

    }

    public AccountEntity get( Long walletId ) {
        Optional<AccountEntity> maybeEntity = repository.findById( walletId );
        if( maybeEntity.isPresent() ) {
            return maybeEntity.get();
        } else {
            throw new RuntimeException( String.format("there is no account[%s]",walletId) );
        }
    }

    public List<AccountEntity> getAll() {
        return repository.findAll();
    }

    @Transactional
    public AccountEntity deposite( Long accountId, BigDecimal amount ) {

        balanceLocker.lock( accountId.toString(), () -> {
            AccountEntity account = get( accountId );
            account.add( amount );
        });
        return get( accountId );

    }

    @Transactional
    public AccountEntity withdraw( Long accountId, BigDecimal amount ) {

        balanceLocker.lock( accountId.toString(), () -> {
            AccountEntity account = checkLackOfBalance( accountId, amount );
            account.subtract( amount );
        });
        return get( accountId );

    }

    @Transactional
    public void transfer( ReqTransfer param ) {

        balanceLocker.lock( param.getFrom().toString(), () -> {

            BigDecimal    amount = param.getAmount();
            AccountEntity from   = checkLackOfBalance( param.getFrom(), amount );
            AccountEntity to     = get( param.getTo()   );

            from.subtract( amount );
            to.add( amount );

        });

    }

    private AccountEntity checkLackOfBalance( Long accountId, BigDecimal amount ) {

        AccountEntity account = get( accountId );

        if( account.getBalance().subtract(amount).compareTo( BigDecimal.ZERO ) < 0 ) {
            throw new IllegalArgumentException(
                String.format( "lack of balance (account:%s, current:%s, amount:%s)",
                    account.getAccountId(), account.getBalance(), amount )
            );
        }

        return account;

    }


}
