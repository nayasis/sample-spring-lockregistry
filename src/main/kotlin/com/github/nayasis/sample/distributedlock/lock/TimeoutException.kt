package com.github.nayasis.sample.distributedlock.lock

import java.lang.RuntimeException

class TimeoutException(message: String, cause: Throwable? = null): RuntimeException(message, cause)