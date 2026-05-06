package com.harishkannarao.restdatarabbitmq.publisher;

import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.harishkannarao.restdatarabbitmq.constants.MessageConstants.*;

@Component
public class MessagePublisher {

    private final JsonConverter jsonConverter;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final MessagePropertiesHolder properties;

    @Autowired
    public MessagePublisher(JsonConverter jsonConverter,
                            RabbitMessagingTemplate rabbitMessagingTemplate,
                            MessagePropertiesHolder properties) {
        this.jsonConverter = jsonConverter;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.properties = properties;
    }

    public void sendToOutboundQueue(SampleMessage sampleMessage) {
        String outboundMessage = jsonConverter.toJson(List.of(sampleMessage));
        Map<String, Object> headers = Map.of(X_CORRELATION_ID, sampleMessage.getId());
        rabbitMessagingTemplate.convertAndSend(
                properties.outboundTopicExchange(),
                properties.outboundRoutingKey(),
                outboundMessage,
                headers);
    }

    public void sendToRetryQueue(UUID correlationId, Integer count, Instant msgExpiry, Instant msgNextRetry, String message) {
        Map<String, Object> requeueHeaders = Map.ofEntries(
                Map.entry(X_CORRELATION_ID, correlationId),
                Map.entry(X_COUNT, count),
                Map.entry(X_MESSAGE_NEXT_RETRY, msgNextRetry),
                Map.entry(X_MESSAGE_EXPIRY, msgExpiry)
        );
        rabbitMessagingTemplate.convertAndSend(
                properties.inboundRetryTopicExchange(),
                properties.inboundRetryRoutingKey(),
                message,
                requeueHeaders);
    }

    public void sendToMainQueue(UUID correlationId, Integer count, Instant msgExpiry, String message) {
        Map<String, Object> retryHeaders = Map.ofEntries(
                Map.entry(X_CORRELATION_ID, correlationId),
                Map.entry(X_COUNT, count),
                Map.entry(X_MESSAGE_EXPIRY, msgExpiry)
        );
        rabbitMessagingTemplate.convertAndSend(
                properties.inboundTopicExchange(),
                properties.inboundRoutingKey(),
                message,
                retryHeaders
        );
    }
}
