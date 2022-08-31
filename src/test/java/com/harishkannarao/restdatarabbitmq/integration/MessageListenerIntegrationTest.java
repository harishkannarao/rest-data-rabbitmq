package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.listener.TestMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class MessageListenerIntegrationTest extends AbstractBaseIntegrationTest {

    private JsonConverter jsonConverter;
    private String topicExchange;
    private String routingKey;

    @BeforeEach
    public void setUp() {
        jsonConverter = getBean(JsonConverter.class);
        topicExchange = getProperty("messaging.message-processor.inbound-topic-exchange");
        routingKey = getProperty("messaging.message-processor.inbound-routing-key");

        TestMessageListener.HOLDER.clear();
    }

    @Test
    public void sendAndReceiveMessages() {
        // given
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World")
                .build();
        String message = jsonConverter.toJson(List.of(sampleMessage));

        // when
        rabbitTemplate().convertAndSend(topicExchange, routingKey, message);

        // then
        await().atMost(Duration.ofSeconds(5)).until(() -> TestMessageListener.HOLDER.containsKey(sampleMessage.getId()));
        var result = TestMessageListener.HOLDER.get(sampleMessage.getId());
        assertThat(result.getValue()).isEqualTo("Hello World");
    }
}
