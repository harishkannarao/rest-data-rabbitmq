package com.harishkannarao.restdatarabbitmq.steps.hook;

import com.harishkannarao.restdatarabbitmq.runner.MySqlTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import io.cucumber.java.Before;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static com.harishkannarao.restdatarabbitmq.runner.SpringBootIntegrationProperties.createIntegrationTestProperties;

public class Hooks {
    @Before
    public void globalSetup() {
        if (!MySqlTestRunner.isRunning()) {
            MySqlTestRunner.start();
        }
        if (!RabbitMqTestRunner.isRunning()) {
            RabbitMqTestRunner.start();
        }
        if (!SpringBootTestRunner.isRunning()) {
            SpringBootTestRunner.start(createIntegrationTestProperties());
        } else if (!createIntegrationTestProperties().equals(SpringBootTestRunner.getProperties())) {
            SpringBootTestRunner.restart(createIntegrationTestProperties());
        }
        RabbitAdmin rabbitAdmin = SpringBootTestRunner.getBean(RabbitAdmin.class);
        rabbitAdmin.purgeQueue("test-queue-1", false);
        rabbitAdmin.purgeQueue("test-queue-2", false);
    }
}
