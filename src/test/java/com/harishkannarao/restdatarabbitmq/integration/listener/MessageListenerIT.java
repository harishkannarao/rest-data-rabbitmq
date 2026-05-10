package com.harishkannarao.restdatarabbitmq.integration.listener;

import com.harishkannarao.restdatarabbitmq.config.MockMessagePublisher;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.integration.AbstractBaseIntegration;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.listener.TestInboundDlqMessageListener;
import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import java.time.Duration;
import java.util.*;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessageListenerIT extends AbstractBaseIntegration {

    private MessagePublisher messagePublisher;
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    private JsonConverter jsonConverter;
    private String inboundTopicExchange;
    private String inboundRoutingKey;

    @Override
    protected Set<Class<?>> additionalConfigurations() {
        return Set.of(MockMessagePublisher.class);
    }

    @Override
    protected Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("test.store-received-messages", "true");
        return properties;
    }

    @BeforeEach
    public void setUp() {
        messagePublisher = getBean(MessagePublisher.class);
        rabbitMessagingTemplate = getBean(RabbitMessagingTemplate.class);
        jsonConverter = getBean(JsonConverter.class);
        inboundTopicExchange = getProperty("messaging.message-processor.inbound-topic-exchange");
        inboundRoutingKey = getProperty("messaging.message-processor.inbound-routing-key");
    }

    @Test
    public void successfully_process_inbound_message() {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World" + UUID.randomUUID())
                .build();
        List<SampleMessage> inputMessages = List.of(sampleMessage);
        String message = jsonConverter.toJson(inputMessages);
        Map<String, Object> headers = Map.of("X-Correlation-ID", sampleMessage.getId());
        rabbitMessagingTemplate.convertAndSend(inboundTopicExchange, inboundRoutingKey, message, headers);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() ->
                        verify(messagePublisher).sendToOutboundQueue(eq(sampleMessage)));
    }

    @Test
    public void message_sent_to_dead_letter_queue_when_exception_during_retry_attempt() {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World" + UUID.randomUUID())
                .build();
        List<SampleMessage> inputMessages = List.of(sampleMessage);
        String message = jsonConverter.toJson(inputMessages);
        Map<String, Object> headers = Map.of("X-Correlation-ID", sampleMessage.getId());

        doThrow(new RuntimeException("outbound publish error"))
                .when(messagePublisher)
                .sendToOutboundQueue(any());

        doThrow(new RuntimeException("retry publish error"))
                .when(messagePublisher)
                .sendToRetryQueue(any(), any(), any(), any(), any());

        rabbitMessagingTemplate.convertAndSend(inboundTopicExchange, inboundRoutingKey, message, headers);

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(Map.copyOf(TestInboundDlqMessageListener.HOLDER))
                        .containsEntry(sampleMessage.getId(), message));

        verify(messagePublisher)
                .sendToOutboundQueue(eq(sampleMessage));

        verify(messagePublisher)
                .sendToRetryQueue(
                        eq(sampleMessage.getId()),
                        eq(2),
                        assertArg(expiry ->
                                assertThat(expiry)
                                        .isBetween(now().plusSeconds(18), now().plusSeconds(22))),
                        assertArg(nextRetry ->
                                assertThat(nextRetry)
                                        .isBetween(now().plusSeconds(3), now().plusSeconds(7))),
                        eq(message));
    }

    @Test
    public void message_sent_to_dead_letter_queue_when_missing_mandatory_header() {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World" + UUID.randomUUID())
                .build();
        List<SampleMessage> inputMessages = List.of(sampleMessage);
        String message = jsonConverter.toJson(inputMessages);
        Map<String, Object> emptyHeaders = Collections.emptyMap();

        rabbitMessagingTemplate.convertAndSend(inboundTopicExchange, inboundRoutingKey, message, emptyHeaders);

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(Map.copyOf(TestInboundDlqMessageListener.HOLDER))
                        .containsEntry(sampleMessage.getId(), message));

        verifyNoInteractions(messagePublisher);
    }
}
