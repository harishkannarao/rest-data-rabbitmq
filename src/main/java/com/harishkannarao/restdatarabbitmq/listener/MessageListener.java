package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.harishkannarao.restdatarabbitmq.constants.MessageConstants.*;

@Component
public class MessageListener {

    private final JsonConverter jsonConverter;
    private final MessagePublisher messagePublisher;
    private final MessagePropertiesHolder properties;

    @Autowired
    public MessageListener(JsonConverter jsonConverter,
                           MessagePublisher messagePublisher,
                           MessagePropertiesHolder properties
    ) {
        this.jsonConverter = jsonConverter;
        this.messagePublisher = messagePublisher;
        this.properties = properties;
    }

    @RabbitListener(
            queues = "${messaging.message-processor.inbound-queue}",
            concurrency = "${messaging.message-processor.inbound-queue-concurrency}")
    public void handleMessage(
            @Header(X_CORRELATION_ID) final UUID correlationId,
            @Header(value = X_COUNT, required = false) final Integer headerCount,
            @Header(value = X_MESSAGE_EXPIRY, required = false) final Instant headerMsgExpiry,
            @Payload final String message) {
        MDC.put(X_CORRELATION_ID, correlationId.toString());
        final int count = Optional.ofNullable(headerCount).orElse(1);
        try {
            SampleMessage[] sampleMessages = jsonConverter.fromJson(message, SampleMessage[].class);
            for (SampleMessage sampleMessage : sampleMessages) {
                LOGGER.info("Received Message: {} {} {} {}", correlationId, count, headerMsgExpiry, sampleMessage.toString());
                if (sampleMessage.getValue().contains("$") && count <= sampleMessage.getValue().length()) {
                    throw new RuntimeException("Artificial Exception");
                }
                messagePublisher.sendToOutboundQueue(sampleMessage);
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
            messagePublisher.sendToRetryQueue(correlationId, updatedCount, msgExpiry, nextRetryInstant, message);
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
            @Payload final String message) {
        try {
            MDC.put(X_CORRELATION_ID, correlationId.toString());
            LOGGER.debug("Received Message for retry: {} {} {} {} {}", correlationId, count, msgExpiry, msgNextRetry, message);
            final Instant currentTime = Instant.now();
            if (msgExpiry.isBefore(currentTime)) {
                LOGGER.info("Message expired: {} {} {} {} {}", correlationId, count, msgExpiry, msgNextRetry, message);
            } else if (msgNextRetry.isBefore(currentTime)) {
                LOGGER.info("Sending message to main queue: {} {} {} {} {}", correlationId, count, msgExpiry, msgNextRetry, message);
                messagePublisher.sendToMainQueue(correlationId, count, msgExpiry, message);
            } else {
                LOGGER.debug("Re-queue message in retry queue: {} {}", correlationId, message);
                messagePublisher.sendToRetryQueue(correlationId, count, msgExpiry, msgNextRetry, message);
            }
        } catch (Exception e) {
            LOGGER.error("Message Processing failed during retry and sending once again for retry", e);
            long seconds = new BigDecimal(properties.multiplicationFactor())
                    .pow(count)
                    .multiply(new BigDecimal(properties.nextRetryDuration().getSeconds()))
                    .longValue();
            final Instant nextRetryInstant = Instant.now().plusSeconds(seconds);
            messagePublisher.sendToRetryQueue(correlationId, count, msgExpiry, nextRetryInstant, message);
        } finally {
            MDC.clear();
        }
    }
}
