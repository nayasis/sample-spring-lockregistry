package io.nayasis.sample.registrylock.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@EnableCaching
@EnableSpringConfigured
@SpringBootApplication
@Slf4j
public class BootApplication implements CommandLineRunner {

    public static void main( String[] args ) {
        SpringApplication.run( BootApplication.class, args );
    }

    @Override
    public void run( String... args ) throws Exception {

    }

}
