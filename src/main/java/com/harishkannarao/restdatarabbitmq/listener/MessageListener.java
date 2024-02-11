package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Component
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
    private static final String X_CORRELATION_ID = "X-Correlation-ID";
    private static final String X_COUNT = "X-Count";
    private static final String X_MESSAGE_TIMESTAMP = "X-Message-Timestamp";
    private final JsonConverter jsonConverter;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final String outboundTopicExchange;
    private final String outboundRoutingKey;
    private final String inboundRetryTopicExchange;
    private final String inboundRetryRoutingKey;

    @Autowired
    public MessageListener(JsonConverter jsonConverter,
                           RabbitMessagingTemplate rabbitMessagingTemplate,
                           @Value("${messaging.message-processor.outbound-topic-exchange}") String outboundTopicExchange,
                           @Value("${messaging.message-processor.outbound-routing-key}") String outboundRoutingKey,
                           @Value("${messaging.message-processor.inbound-retry-topic-exchange}") String inboundRetryTopicExchange,
                           @Value("${messaging.message-processor.inbound-retry-routing-key}") String inboundRetryRoutingKey
    ) {
        this.jsonConverter = jsonConverter;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.outboundTopicExchange = outboundTopicExchange;
        this.outboundRoutingKey = outboundRoutingKey;
        this.inboundRetryTopicExchange = inboundRetryTopicExchange;
        this.inboundRetryRoutingKey = inboundRetryRoutingKey;
    }

    @RabbitListener(
            queues = "${messaging.message-processor.inbound-queue}",
            concurrency = "${messaging.message-processor.inbound-queue-concurrency}")
    public void handleMessage(
            @Header(X_CORRELATION_ID) final UUID correlationId,
            @Header(value = X_COUNT, required = false) final Integer headerCount,
            @Header(value = X_MESSAGE_TIMESTAMP, required = false) final Instant headerMsgTimestamp,
            final String message) {
        MDC.put(X_CORRELATION_ID, correlationId.toString());
        final Integer count = Optional.ofNullable(headerCount).orElse(0);
        final Instant resolvedTimestamp = Optional.ofNullable(headerMsgTimestamp).orElseGet(Instant::now);
        try {
            List<SampleMessage> sampleMessages = Arrays.asList(jsonConverter.fromJson(message, SampleMessage[].class));
            for (SampleMessage sampleMessage : sampleMessages) {
                LOGGER.info("Received Message: {} {}", correlationId, sampleMessage.toString());
                if (sampleMessage.getValue().contains("$") && count <= sampleMessage.getValue().length()) {
                    throw new RuntimeException("Artificial Exception");
                }
                String outboundMessage = jsonConverter.toJson(List.of(sampleMessage));
                Map<String, Object> headers = Map.of(X_CORRELATION_ID, sampleMessage.getId());
                rabbitMessagingTemplate.convertAndSend(outboundTopicExchange, outboundRoutingKey, outboundMessage, headers);
            }
        } catch (Exception e) {
            LOGGER.error("Message Processing failed and sending for retry", e);
            Map<String, Object> retryHeaders = Map.ofEntries(
                    Map.entry(X_CORRELATION_ID, correlationId),
                    Map.entry(X_COUNT, count),
                    Map.entry(X_MESSAGE_TIMESTAMP, resolvedTimestamp)
            );
            rabbitMessagingTemplate.convertAndSend(
                    inboundRetryTopicExchange,
                    inboundRetryRoutingKey,
                    message,
                    retryHeaders
            );
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
            @Header(value = X_MESSAGE_TIMESTAMP) final Instant msgTimestamp,
            final String message) {
        try {
            MDC.put(X_CORRELATION_ID, correlationId.toString());
            LOGGER.info("Received Message for retry: {} {} {} {}", correlationId, count, msgTimestamp, message);
        } finally {
            MDC.clear();
        }
    }
}
