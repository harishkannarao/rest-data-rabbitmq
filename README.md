# REST Data RabbitMq
This project demonstrates the REST endpoints backed by Spring Data CRUD repository along with RabbitMq messaging interfaces (producer and listener) and Cucumber for functional integration tests.

# Getting Started

### Required Tools

* Java 17
* Maven 3.5.3
* Docker Engine Latest Version

### Run Build

    mvn clean install

### Run application

    mvn spring-boot:run

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
