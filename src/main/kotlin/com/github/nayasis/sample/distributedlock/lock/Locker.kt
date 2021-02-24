@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")

package com.github.nayasis.sample.distributedlock.lock

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.integration.support.locks.LockRegistry
import org.springframework.stereotype.Component
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

@Component
class Locker(
    val lockRegistry: LockRegistry,
    @Value("\${distributed-lock.timeout.connection:1000}")
    val connectionTimeout: Long,
    @Value("\${distributed-lock.retry.count:10}")
    val retryCount: Int,
    @Value("\${distributed-lock.retry.sleep:200}")
    val retrySleep: Long,
) {

    fun <T> lock(lockKey:String, fn:() -> T): T {
        val lock = tryLock(lockKey)
        try {
            return fn()
        } finally {
            try { lock.unlock() } catch (e: Exception) {}
        }
    }

    private fun tryLock(lockKey: String): Lock {
        val lock = lockRegistry.obtain(lockKey)
        for( i in 0..retryCount ) {
            if( i != 0 )
                sleep( retrySleep )
            if( lock.tryLock(connectionTimeout, TimeUnit.MILLISECONDS) )
                return lock
        }
        throw TimeoutException("Fail to acquire lock.")
    }

}