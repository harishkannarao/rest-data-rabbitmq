package com.harishkannarao.restdatarabbitmq.config;

import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockMessagePublisher {

    private final MessagePublisher mockMessagePublisher = Mockito.mock();

    @Bean(name = "messagePublisher")
    @Primary
    public MessagePublisher mockMessagePublisher() {
        return mockMessagePublisher;
    }

    @Bean
    public MockitoMockHolder messagePublisherHolder() {
        return new MockitoMockHolder(mockMessagePublisher);
    }
}
