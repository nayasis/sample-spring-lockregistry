package com.github.nayasis.sample.distributedlock.lock

import org.springframework.integration.support.locks.LockRegistry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class LocalLockRegistry: LockRegistry {

    private val locks = ConcurrentHashMap<String, ReentrantLock>()

    override fun obtain(lockKey: Any?): Lock {
        require( lockKey is String )
        return locks.getOrPut(lockKey){ReentrantLock()}
    }

}