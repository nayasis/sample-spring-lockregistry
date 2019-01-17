Redis LockRegistry sample
=========================
spring boot sample for redis lock registry

## LockRegistry
[LockRegistry](https://github.com/spring-cloud/spring-cloud-cluster/blob/master/spring-cloud-cluster-core/src/main/java/org/springframework/cloud/cluster/lock/LockRegistry.java) is one of spring cloud interface controlling Application lock.
Application lock is similiar with ThreadLock but works between multi-instances so help to control race condition, for example in DBMS CRUD operation.

## Requirement
This sample use redis lock registry implement. so if you want to run it, you must install Redis at first.

## Port

---------------------------
| Server | 8080           |
---------------------------
| Server | 8080           |
| Redis  | 6379 (default) |
---------------------------


## What it is ?
It is simple sample 



