package io.nayasis.sample.registrylock.redis.core.exception;

public class LockInterruptedException extends RuntimeException {

    public LockInterruptedException() {
    }

    public LockInterruptedException( String message ) {
        super( message );
    }

    public LockInterruptedException( String message, Throwable cause ) {
        super( message, cause );
    }

    public LockInterruptedException( Throwable cause ) {
        super( cause );
    }

    public LockInterruptedException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }

}