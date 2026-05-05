package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.RestDataRabbitmqApplication;
import com.harishkannarao.restdatarabbitmq.config.RabbitMqConfiguration;
import com.harishkannarao.restdatarabbitmq.runner.MySqlTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringSettings;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractBaseIntegration {

    @BeforeEach
    public void applicationBootStrap() {
        startDependencies();

        final Properties properties = createProperties();
        Set<Class<?>> sources = Set.of(
                RestDataRabbitmqApplication.class,
                RabbitMqConfiguration.class);
        final SpringSettings settings = new SpringSettings(sources, properties);
        SpringBootTestRunner.bootStrap(settings);

        RabbitAdmin rabbitAdmin = SpringBootTestRunner.getBean(RabbitAdmin.class);
        rabbitAdmin.purgeQueue("test-queue-1", false);
        rabbitAdmin.purgeQueue("test-queue-2", false);
    }

    private static Properties createProperties() {
        final Properties properties = new Properties();
        properties.setProperty("server.port", "0");
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
        return properties;
    }

    private static void startDependencies() {
        Supplier<Boolean> mySqlStarter = () -> {
            if (!MySqlTestRunner.isRunning()) {
                MySqlTestRunner.start(true);
            }
            return true;
        };
        Supplier<Boolean> rabbitMqStarter = () -> {
            if (!RabbitMqTestRunner.isRunning()) {
                RabbitMqTestRunner.start(true);
            }
            return true;
        };
        Stream.of(mySqlStarter, rabbitMqStarter)
                .parallel()
                .map(Supplier::get)
                .forEach(_ -> {});
    }
}
