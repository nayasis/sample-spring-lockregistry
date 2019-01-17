Redis LockRegistry sample
=========================
spring boot sample for redis lock registry

## LockRegistry
[LockRegistry](https://github.com/spring-cloud/spring-cloud-cluster/blob/master/spring-cloud-cluster-core/src/main/java/org/springframework/cloud/cluster/lock/LockRegistry.java) is one of spring cloud interface controlling Application lock.

Application lock is similiar with ThreadLock but works between multi-instances so help to control race condition, for example in DBMS CRUD operation.

## Requirement
This sample use [RedisLockRegistry](https://github.com/spring-projects/spring-integration/blob/master/spring-integration-redis/src/main/java/org/springframework/integration/redis/util/RedisLockRegistry.java) implement. so if you want to run it, you must install Redis at first.

## Port in use

| Kind   | Port           |
|--------|----------------|
| Server | 8080           |
| Redis  | 6379 (default) |

## Business logic

It is simple account/balance model in bank.

User can create account and deposite/withdraw balance and transfer balance to another.

You can use postman sample in [HERE](https://github.com/nayasis/sample.redislockregistry/blob/master/postman/RedisRegistryLock.postman_collection.json).
