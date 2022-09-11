package com.harishkannarao.restdatarabbitmq.steps.hook;

import com.harishkannarao.restdatarabbitmq.runner.PostgresTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import io.cucumber.java.Before;

import static com.harishkannarao.restdatarabbitmq.runner.SpringBootIntegrationProperties.createIntegrationTestProperties;

public class Hooks {
    @Before
    public void globalSetup() {
        if (!PostgresTestRunner.isRunning()) {
            PostgresTestRunner.start();
        }
        if (!RabbitMqTestRunner.isRunning()) {
            RabbitMqTestRunner.start();
        }
        if (!SpringBootTestRunner.isRunning()) {
            SpringBootTestRunner.start(createIntegrationTestProperties());
        } else if (!createIntegrationTestProperties().equals(SpringBootTestRunner.getProperties())) {
            SpringBootTestRunner.restart(createIntegrationTestProperties());
        }
    }
}
