package com.harishkannarao.restdatarabbitmq.steps.hook;

import com.harishkannarao.restdatarabbitmq.runner.MySqlTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import io.cucumber.java.Before;

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
    }
}
