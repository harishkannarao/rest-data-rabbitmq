package com.harishkannarao.restdatarabbitmq.steps.hook;

import com.harishkannarao.restdatarabbitmq.runner.MySqlTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import com.harishkannarao.restdatarabbitmq.steps.holder.LogbackAppenderHolder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Hooks {

    private final Properties properties = new Properties();
    private final LogbackAppenderHolder logbackAppenderHolder;

    public Hooks(LogbackAppenderHolder logbackAppenderHolder) {
        this.logbackAppenderHolder = logbackAppenderHolder;
    }

    @Before(order = 1)
    public void setDefaultProperties() {
        properties.setProperty("server.port", "0");
        properties.setProperty("spring.profiles.active", "int-test");
    }

    @Before(order = 2, value = "@FeatureDisableStudentApi")
    public void featureDisableStudentApi() {
        properties.setProperty("feature.api.student.enabled", "false");
    }

    @Before(order = 3)
    public void startServices() {
        Supplier<Boolean> mySqlStarter = () -> {
            if (!MySqlTestRunner.isRunning()) {
                MySqlTestRunner.start(false);
            }
            return true;
        };
        Supplier<Boolean> rabbitMqStarter = () -> {
            if (!RabbitMqTestRunner.isRunning()) {
                RabbitMqTestRunner.start(false);
            }
            return true;
        };
        Stream.of(mySqlStarter, rabbitMqStarter)
                .parallel()
                .map(Supplier::get)
                .forEach(aBoolean -> {
                });
    }

    @Before(order = 4)
    public void setProperties() {
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
    }

    @Before(order = 5)
    public void bootStrap() {
        if (!SpringBootTestRunner.isRunning()) {
            SpringBootTestRunner.start(properties);
        } else if (!properties.equals(SpringBootTestRunner.getProperties())) {
            SpringBootTestRunner.restart(properties);
        }
    }


    @Before(order = 6)
    public void startLogAppender() {
        logbackAppenderHolder.getMessageListenerAppender().startLogsCapture();
    }

    @Before(order = 7)
    public void clearRabbitMq() {
        RabbitAdmin rabbitAdmin = SpringBootTestRunner.getBean(RabbitAdmin.class);
        rabbitAdmin.purgeQueue("test-queue-1", false);
        rabbitAdmin.purgeQueue("test-queue-2", false);
    }

    @After(order = 1)
    public void stopLogAppender() {
        logbackAppenderHolder.getMessageListenerAppender().stopLogsCapture();
    }
}
