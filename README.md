# REST Data RabbitMq
This project demonstrates the REST endpoints backed by Spring Data CRUD repository along with RabbitMq messaging interfaces (producer and listener) and Cucumber for functional integration tests.

# Getting Started

### Required Tools

* Java 21
* Maven 3.5.3
* Docker Engine Latest Version

### Run Build

    mvn clean install

### Run application

    mvn spring-boot:run

### Run application with MySql and RabbitMq

    mvn clean test-compile exec:java@run-local

Send sample messages

    curl -s 'http://localhost:8080/send-messages/success/1'

    curl -s 'http://localhost:8080/send-messages/retry-and-succeed/1'

    curl -s 'http://localhost:8080/send-messages/retry-and-expire/1'

Rabbit MQ Management Portal

    http://localhost:15672

username: guest
password: guest

### Run only unit tests

    mvn clean install -DskipITs

### Run only integration tests (cucumber functional)

    mvn clean install -Dsurefire.failIfNoSpecifiedTests=false -Dtest=skip-unit-tests

### Skip all tests

    mvn clean install -DskipTests

### Update all dependencies

    mvn versions:use-latest-versions

    mvn versions:use-latest-releases

    mvn versions:use-latest-snapshots

    mvn versions:update-properties

    mvn versions:update-child-modules

    mvn versions:update-parent

### Open rewrite command

    mvn rewrite:run