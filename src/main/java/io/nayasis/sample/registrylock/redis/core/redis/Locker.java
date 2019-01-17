package io.nayasis.sample.registrylock.redis.core.redis;

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

    public boolean lock( String lockKey, Runnable runnable ) {

        Lock lock = lockRegistry.obtain( lockKey );

        try {

            boolean acquired = lock.tryLock( 10, TimeUnit.SECONDS );

            if( acquired ) {
                runnable.run();
                return true;
            }
            return false;

        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new RuntimeException( String.format( "Interrupted in locking [%s]", registryKey) );
        } finally {
            lock.unlock();
        }

    }

}
