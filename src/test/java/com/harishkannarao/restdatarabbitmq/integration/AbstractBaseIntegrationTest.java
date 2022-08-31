package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.runner.PostgresTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractBaseIntegrationTest {

    @BeforeEach
    void globalSetup() {
        if (!PostgresTestRunner.isRunning()) {
            PostgresTestRunner.start();
        }
        if (!RabbitMqTestRunner.isRunning()) {
            RabbitMqTestRunner.start();
        }
        if (!SpringBootTestRunner.isRunning()) {
            SpringBootTestRunner.start(getIntegrationTestProperties());
        } else if (!getIntegrationTestProperties().equals(SpringBootTestRunner.getProperties())) {
            SpringBootTestRunner.restart(getIntegrationTestProperties());
        }
    }

    private Properties getIntegrationTestProperties() {
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

        getAdditionalTestProperties().forEach(properties::setProperty);

        return properties;
    }

    protected Map<String, String> getAdditionalTestProperties() {
        return Collections.emptyMap();
    }

    protected <T> T getBean(Class<T> clazz) {
        return SpringBootTestRunner.getBean(clazz);
    }

    protected String getProperty(String key) {
        return getBean(Environment.class).getProperty(key);
    }

    protected RabbitTemplate rabbitTemplate() {
        return getBean(RabbitTemplate.class);
    }
}
