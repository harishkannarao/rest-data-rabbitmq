package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class AbstractBaseSteps {

    protected <T> T getBean(Class<T> clazz) {
        return SpringBootTestRunner.getBean(clazz);
    }

    protected String getProperty(String key) {
        return getBean(Environment.class).getProperty(key);
    }

    protected RabbitMessagingTemplate rabbitMessagingTemplate() {
        return getBean(RabbitMessagingTemplate.class);
    }

    protected String applicationUrl() {
        return SpringBootTestRunner.getApplicationUrl();
    }

    protected WebTestClient webTestClient() {
        return WebTestClient.bindToServer().build();
    }

    protected JsonConverter jsonConverter() {
        return getBean(JsonConverter.class);
    }
}
