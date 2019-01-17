package io.nayasis.sample.registrylock.redis.core.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class LockerInitializer {

    @Bean
    public Locker balanceLocker( RedisConnectionFactory connectionFactory ) {
        return new Locker( connectionFactory, "balance" );
    }

}
