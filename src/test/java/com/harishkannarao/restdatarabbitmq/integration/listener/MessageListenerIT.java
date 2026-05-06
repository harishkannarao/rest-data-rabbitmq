package com.harishkannarao.restdatarabbitmq.integration.listener;

import com.harishkannarao.restdatarabbitmq.config.MockMessagePublisher;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.integration.AbstractBaseIntegration;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

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

    @BeforeEach
    public void setUp() {
        messagePublisher = getBean(MessagePublisher.class);
        reset(messagePublisher);
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
}
