package io.nayasis.sample.registrylock.redis.core.redis;

import io.nayasis.sample.registrylock.redis.core.exception.LockInterruptedException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Locker {

    private LockRegistry lockRegistry;
    private String       registryKey;

    public Locker( RedisConnectionFactory connectionFactory, String registryKey ) {
        this.registryKey  = registryKey;
        this.lockRegistry = new RedisLockRegistry( connectionFactory, registryKey, 10_000 );
    }

    public void lock( String lockKey, Runnable runnable ) throws LockInterruptedException {

        Lock lock = lockRegistry.obtain( lockKey );

        try {

            boolean acquired = lock.tryLock( 10, TimeUnit.SECONDS );

            if( ! acquired )
                throw new LockInterruptedException( String.format( "Failed to acquire lock [%s]", registryKey) );

            runnable.run();

        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException( String.format( "Interrupted in locking [%s]", registryKey) );
        } finally {
            lock.unlock();
        }

    }

}
