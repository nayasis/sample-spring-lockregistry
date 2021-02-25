@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")

package com.github.nayasis.sample.distributedlock.lock

import mu.KotlinLogging
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.integration.jdbc.lock.DefaultLockRepository
import org.springframework.integration.jdbc.lock.JdbcLockRegistry
import org.springframework.integration.jdbc.lock.LockRepository
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.integration.support.locks.ExpirableLockRegistry
import org.springframework.integration.support.locks.LockRegistry
import org.springframework.integration.support.locks.PassThruLockRegistry
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.util.StopWatch
import java.net.ConnectException
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

private val log = KotlinLogging.logger {}

private const val ZOOKEEPER = "'zookeeper'"
private const val REDIS     = "'redis'"
private const val JDBC      = "'jdbc'"
private const val LOCAL     = "'local'"

@Configuration
class LockRegistryConfig(
    @Value("\${distributed-lock.timeout.connection:1000}")
    val connectionTimeout: Int,
    @Value("\${distributed-lock.timeout.session:30000}")
    val sessionTimeout: Int,
    @Value("\${distributed-lock.key-prefix:}")
    val prefix: String,
) {

    @Bean
    @ConditionalOnExpression("{$ZOOKEEPER,$REDIS,$JDBC,$LOCAL}.contains('\${distributed-lock.type}'.toLowerCase()) == false")
    fun emptyLockRegistry(): LockRegistry {
        return PassThruLockRegistry().also { log.info { "Pass-through LockRegistry activated." } }
    }

    @Bean
    @ConditionalOnExpression("'\${distributed-lock.type}'.toLowerCase() == $LOCAL")
    fun localLockRegistry(): LockRegistry {
        return LocalLockRegistry().also { log.info { "Local LockRegistry activated." } }
    }

    @Bean
    @ConditionalOnExpression("'\${spring.zookeeper.address:}' != ''")
    fun zookeeperClient(
        @Value("\${spring.zookeeper.address}")
        address: String,
    ): CuratorFramework {
        val noRetry = RetryNTimes(0, 0)
        return CuratorFrameworkFactory.newClient(address, connectionTimeout, sessionTimeout, noRetry).apply {
            start()
            if( ! blockUntilConnected(connectionTimeout, TimeUnit.MILLISECONDS) )
                throw ConnectException("Fail to connect zookeeper ($address)")
        }
    }

    @Bean
    @ConditionalOnExpression("'\${distributed-lock.type}'.toLowerCase() == $ZOOKEEPER")
    fun zookeeperLockRegistry(
        client: CuratorFramework,
    ): ZookeeperLockRegistry {
        return ZookeeperLockRegistry(client, "/${prefix.replace("^/".toRegex(), "")}")
            .also { log.info { "Zookeeper LockRegistry activated." } }
    }

    @Bean
    @ConditionalOnExpression("'\${distributed-lock.type}'.toLowerCase() == $REDIS")
    fun redisLockRegistry(
        connectionFactory: RedisConnectionFactory,
    ): RedisLockRegistry {
        return RedisLockRegistry(connectionFactory, prefix, sessionTimeout.toLong())
            .also { log.info { "Redis LockRegistry activated." } }
    }

    @Bean
    @ConditionalOnExpression("'\${distributed-lock.type}'.toLowerCase() == $JDBC")
    fun jdbcLockRepository(
        dataSource: DataSource,
        @Value("\${distributed-lock.jdbc.table-prefix:}")
        tablePrefix: String,
    ): DefaultLockRepository {
        return if(prefix.isNotEmpty()) {
            DefaultLockRepository(dataSource,prefix)
        } else {
            DefaultLockRepository(dataSource)
        }.apply {
            if( tablePrefix.isNotEmpty() )
                setPrefix(tablePrefix)
            setTimeToLive(sessionTimeout)
        }
    }

    @Bean
    @ConditionalOnExpression("'\${distributed-lock.type}'.toLowerCase() == $JDBC")
    fun jdbcLockRegistry(
        repository: LockRepository,
    ): JdbcLockRegistry {
        return JdbcLockRegistry(repository).also { log.info { "Jdbc LockRegistry activated." } }
    }

}

@Configuration
@ConditionalOnBean(ExpirableLockRegistry::class)
class LockScheduler(
    private val lockRegistry: ExpirableLockRegistry,
    @Value("\${distributed-lock.expire.age:60000}")
    private val expireAge: Int,
) {

    @ConditionalOnExpression("'\${distributed-lock.expire.cron:}' != ''")
    @Scheduled(cron="\${distributed-lock.expire.cron:1 1 1 1 1 *}")
    fun expireUnusedLock() {
        StopWatch("Clear unused lock").let {
            it.start()
            lockRegistry.expireUnusedOlderThan(expireAge.toLong())
            log.trace { it.shortSummary() }
        }
    }

}