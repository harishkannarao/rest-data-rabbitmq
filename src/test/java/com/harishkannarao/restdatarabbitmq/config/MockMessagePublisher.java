package com.harishkannarao.restdatarabbitmq.config;

import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockMessagePublisher {

    @Bean(name = "messagePublisher")
    @Primary
    public MessagePublisher mockMessagePublisher() {
        return Mockito.mock(MessagePublisher.class);
    }
}
