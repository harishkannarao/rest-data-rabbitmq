package com.harishkannarao.restdatarabbitmq.unit.publisher;

import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.harishkannarao.restdatarabbitmq.constants.MessageConstants.X_CORRELATION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MessagePublisherTest {
    private final JsonConverter jsonConverter = mock();
    private final RabbitMessagingTemplate rabbitMessagingTemplate = mock();
    private final MessagePropertiesHolder properties = new MessagePropertiesHolder(
            "out-exchange",
            "out-key",
            "in-exchange",
            "in-key",
            "in-retry-exchange",
            "in-retry-key",
            Duration.ofSeconds(5),
            Duration.ofMinutes(2),
            "1"
    );
    private final MessagePublisher messagePublisher = new MessagePublisher(
            jsonConverter,
            rabbitMessagingTemplate,
            properties
    );

    @Test
    public void publish_to_outbound_queue() {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World" + UUID.randomUUID())
                .build();
        String outMessage = "out-message";

        when(jsonConverter.toJson(any())).thenReturn(outMessage);

        messagePublisher.sendToOutboundQueue(sampleMessage);

        verify(jsonConverter)
                .toJson(
                        assertArg((List<SampleMessage> actual) ->
                                assertThat(actual)
                                        .containsExactlyInAnyOrder(sampleMessage)));

        verify(rabbitMessagingTemplate)
                .convertAndSend(
                        eq(properties.outboundTopicExchange()),
                        eq(properties.outboundRoutingKey()),
                        eq(outMessage),
                        assertArg((Map<String, Object> headers) ->
                                assertThat(headers).containsEntry(X_CORRELATION_ID, sampleMessage.getId())));
    }
}
