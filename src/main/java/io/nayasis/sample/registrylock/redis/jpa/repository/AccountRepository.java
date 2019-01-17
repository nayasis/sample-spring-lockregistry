package io.nayasis.sample.registrylock.redis.jpa.repository;

import io.nayasis.sample.registrylock.redis.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity,Long> {
}
