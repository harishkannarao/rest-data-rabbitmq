package com.harishkannarao.restdatarabbitmq.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Properties;

public class ApplicationLocalRunner {
    public static void main(String[] args) {
        final Logger logger
                = LoggerFactory.getLogger(ApplicationLocalRunner.class);

        if (!MySqlTestRunner.isRunning()) {
            MySqlTestRunner.start();
        }

        if (!RabbitMqTestRunner.isRunning()) {
            RabbitMqTestRunner.start();
        }

        final Properties properties = new Properties();
        properties.setProperty("server.port", "8080");
        properties.setProperty("spring.profiles.active", "int-test");
        properties.setProperty("test.store-received-messages", "false");

        properties.setProperty("spring.datasource.url", MySqlTestRunner.getJdbcUrl());
        properties.setProperty("spring.datasource.username", MySqlTestRunner.getUsername());
        properties.setProperty("spring.datasource.password", MySqlTestRunner.getPassword());
        properties.setProperty("spring.flyway.url", MySqlTestRunner.getJdbcUrl());
        properties.setProperty("spring.flyway.user", MySqlTestRunner.getUsername());
        properties.setProperty("spring.flyway.password", MySqlTestRunner.getPassword());

        properties.setProperty("spring.rabbitmq.host", RabbitMqTestRunner.getHost());
        properties.setProperty("spring.rabbitmq.port", RabbitMqTestRunner.getPort().toString());
        properties.setProperty("spring.rabbitmq.username", RabbitMqTestRunner.getUsername());
        properties.setProperty("spring.rabbitmq.password", RabbitMqTestRunner.getPassword());

        SpringBootTestRunner.start(properties);

        logger.info("Application Started");

        RabbitAdmin rabbitAdmin = SpringBootTestRunner.getBean(RabbitAdmin.class);
        rabbitAdmin.purgeQueue("test-queue-1", false);
        rabbitAdmin.purgeQueue("test-queue-2", false);

        logger.info("Purged Queues");
    }
}
