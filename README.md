LockRegistry sample
=========================
spring boot sample for [LockRegistry](https://github.com/spring-cloud/spring-cloud-cluster/blob/master/spring-cloud-cluster-core/src/main/java/org/springframework/cloud/cluster/lock/LockRegistry.java).

## LockRegistry
[LockRegistry](https://github.com/spring-cloud/spring-cloud-cluster/blob/master/spring-cloud-cluster-core/src/main/java/org/springframework/cloud/cluster/lock/LockRegistry.java) is one of spring cloud interfaces controlling **Distributed application lock.**

**Distributed Application lock** is similar with ThreadLock but works between multi-instances so help to control race condition, for example in DBMS CRUD operation.

## Requirement

### Docker
if you want to use Zookeeper Lock or Redis Lock, you must install Zookeeper or Redis at first.
you can install easily via [Docker](https://docs.docker.com/get-docker/) and [Docker-Compose](https://docs.docker.com/compose/install/).

### Run Zookeeper
```
cd docker/zookeeper
docker-compose up -d
```

### Run Redis
```
cd docker/redis
docker-compose up -d
```

### Run DBMS
This sample use embedded [H2](https://www.h2database.com/html/main.html) so you can test sample without any specific DBMS. 
 
## Test

### Startup Server
```
gradle bootRun
```
Default port is 8080.

### Test API

This sample provides simple test API, <u>/lock/**{lockkey}**</u> .

- source : [LockController.kt](https://github.com/nayasis/sample-spring-lockregistry/blob/master/src/main/kotlin/com/github/nayasis/sample/distributedlock/controller/LockController.kt)

you can call it like below.  
```
curl localhost:8080/lock/TESTKEY
```

API's logic is simple.
It just counts 0 to 9 and sleep 1 sec on each cycle. 

### Choose Lock Registry

edit **distributed-lock.type** on [application.yml](https://github.com/nayasis/sample-spring-lockregistry/blob/master/src/main/resources/application.yml).

| Kind      | Implement                                 |
| --------  | ----------------                          |
| local     | Java Lock (works in single instance only) |
| redis     | Redis                                     |
| zookeeper | Zookeeper                                 |
| jdbc      | JDBC Datasource                           |
| none      | Do not use Lock registry                  |

- if you want to see DB Table layout on JDBC LockRegistry,
refer to [spring-integration-jdbc](https://github.com/spring-projects/spring-integration/tree/v5.3.0.RELEASE/spring-integration-jdbc/src/main/resources/org/springframework/integration/jdbc)

