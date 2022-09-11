package com.harishkannarao.restdatarabbitmq.runner;

import java.util.Properties;

public interface SpringBootIntegrationProperties {
    static Properties createIntegrationTestProperties() {
        Properties properties = new Properties();
        properties.setProperty("server.port", "0");
        properties.setProperty("spring.profiles.active", "int-test");
        properties.setProperty("spring.datasource.url", PostgresTestRunner.getJdbcUrl());
        properties.setProperty("spring.datasource.username", PostgresTestRunner.getUsername());
        properties.setProperty("spring.datasource.password", PostgresTestRunner.getPassword());
        properties.setProperty("spring.flyway.url", PostgresTestRunner.getJdbcUrl());
        properties.setProperty("spring.flyway.user", PostgresTestRunner.getUsername());
        properties.setProperty("spring.flyway.password", PostgresTestRunner.getPassword());
        properties.setProperty("spring.rabbitmq.host", RabbitMqTestRunner.getHost());
        properties.setProperty("spring.rabbitmq.port", RabbitMqTestRunner.getPort().toString());
        properties.setProperty("spring.rabbitmq.username", RabbitMqTestRunner.getUsername());
        properties.setProperty("spring.rabbitmq.password", RabbitMqTestRunner.getPassword());

        return properties;
    }
}
