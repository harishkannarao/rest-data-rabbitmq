package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
    private static final String X_CORRELATION_ID = "X-Correlation-ID";
    private static final String X_COUNT = "X-Count";
    private static final String X_MESSAGE_EXPIRY = "X-Message-Expiry";
    private static final String X_MESSAGE_NEXT_RETRY = "X-Message-Next-Retry";
    private final JsonConverter jsonConverter;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final MessagePropertiesHolder properties;

    @Autowired
    public MessageListener(JsonConverter jsonConverter,
                           RabbitMessagingTemplate rabbitMessagingTemplate,
                           MessagePropertiesHolder properties
    ) {
        this.jsonConverter = jsonConverter;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.properties = properties;
    }

    @RabbitListener(
            queues = "${messaging.message-processor.inbound-queue}",
            concurrency = "${messaging.message-processor.inbound-queue-concurrency}")
    public void handleMessage(
            @Header(X_CORRELATION_ID) final UUID correlationId,
            @Header(value = X_COUNT, required = false) final Integer headerCount,
            @Header(value = X_MESSAGE_EXPIRY, required = false) final Instant headerMsgExpiry,
            final String message) {
        MDC.put(X_CORRELATION_ID, correlationId.toString());
        final int count = Optional.ofNullable(headerCount).orElse(1);
        try {
            SampleMessage[] sampleMessages = jsonConverter.fromJson(message, SampleMessage[].class);
            for (SampleMessage sampleMessage : sampleMessages) {
                LOGGER.info("Received Message: {} {} {} {}", correlationId, count, headerMsgExpiry, sampleMessage.toString());
                if (sampleMessage.getValue().contains("$") && count <= sampleMessage.getValue().length()) {
                    throw new RuntimeException("Artificial Exception");
                }
                String outboundMessage = jsonConverter.toJson(List.of(sampleMessage));
                Map<String, Object> headers = Map.of(X_CORRELATION_ID, sampleMessage.getId());
                rabbitMessagingTemplate.convertAndSend(
                        properties.outboundTopicExchange(),
                        properties.outboundRoutingKey(),
                        outboundMessage,
                        headers);
            }
        } catch (Exception e) {
            LOGGER.error("Message Processing failed", e);
            long seconds = new BigDecimal(properties.multiplicationFactor())
                    .pow(count)
                    .multiply(new BigDecimal(properties.nextRetryDuration().getSeconds()))
                    .longValue();
            final int updatedCount = count + 1;
            final Instant nextRetryInstant = Instant.now().plusSeconds(seconds);
            final Instant msgExpiry = Optional.ofNullable(headerMsgExpiry)
                    .orElseGet(() -> Instant.now().plus(properties.msgExpiryDuration()));
            LOGGER.info("Sending message for retry queue: {} {} {} {} {}", correlationId, updatedCount, headerMsgExpiry, nextRetryInstant, message);
            sendToRetryQueue(correlationId, updatedCount, msgExpiry, nextRetryInstant, message);
        } finally {
            MDC.clear();
        }
    }

    @RabbitListener(
            queues = "${messaging.message-processor.inbound-retry-queue}",
            concurrency = "${messaging.message-processor.inbound-retry-queue-concurrency}")
    public void handleRetry(
            @Header(X_CORRELATION_ID) UUID correlationId,
            @Header(value = X_COUNT) final Integer count,
            @Header(value = X_MESSAGE_EXPIRY) final Instant msgExpiry,
            @Header(value = X_MESSAGE_NEXT_RETRY) final Instant msgNextRetry,
            final String message) {
        try {
            MDC.put(X_CORRELATION_ID, correlationId.toString());
            LOGGER.debug("Received Message for retry: {} {} {} {} {}", correlationId, count, msgExpiry, msgNextRetry, message);
            final Instant currentTime = Instant.now();
            if (msgExpiry.isBefore(currentTime)) {
                LOGGER.info("Message expired: {} {} {} {} {}", correlationId, count, msgExpiry, msgNextRetry, message);
            } else if (msgNextRetry.isBefore(currentTime)) {
                LOGGER.info("Sending message to main queue: {} {} {} {} {}", correlationId, count, msgExpiry, msgNextRetry, message);
                sendToMainQueue(correlationId, count, msgExpiry, message);
            } else {
                LOGGER.debug("Re-queue message in retry queue: {} {}", correlationId, message);
                sendToRetryQueue(correlationId, count, msgExpiry, msgNextRetry, message);
            }
        } catch (Exception e) {
            LOGGER.error("Message Processing failed during retry and sending for retry", e);
            sendToRetryQueue(correlationId, count, msgExpiry, msgNextRetry, message);
        } finally {
            MDC.clear();
        }
    }

    private void sendToRetryQueue(UUID correlationId, Integer count, Instant msgExpiry, Instant msgNextRetry, String message) {
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

    private void sendToMainQueue(UUID correlationId, Integer count, Instant msgExpiry, String message) {
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
