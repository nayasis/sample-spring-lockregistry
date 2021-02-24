package com.github.nayasis.sample.distributedlock.controller

import com.github.nayasis.sample.distributedlock.lock.Locker
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Thread.sleep

private val log = KotlinLogging.logger {}

@RestController
class LockController(
    private val locker: Locker
) {

    private var sequence = 0

    @RequestMapping("/lock/{key}")
    fun doLock( @PathVariable key: String ) {
        val seq = sequence++
        log.debug { ">> request ($seq)" }
        locker.lock(key) {
            for( i in 0..9 ) {
                log.debug { "- key:[$key],\tsequence:[$seq],\tcount:[$i]" }
                sleep(1_000)
            }
        }
    }

}