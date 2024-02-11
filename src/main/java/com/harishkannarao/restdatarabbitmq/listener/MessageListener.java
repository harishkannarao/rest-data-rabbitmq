package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
    private static final String X_CORRELATION_ID = "X-Correlation-ID";
    private final JsonConverter jsonConverter;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final String outboundTopicExchange;
    private final String outboundRoutingKey;

    @Autowired
    public MessageListener(JsonConverter jsonConverter,
                           RabbitMessagingTemplate rabbitMessagingTemplate,
                           @Value("${messaging.message-processor.outbound-topic-exchange}") String outboundTopicExchange,
                           @Value("${messaging.message-processor.outbound-routing-key}") String outboundRoutingKey) {
        this.jsonConverter = jsonConverter;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.outboundTopicExchange = outboundTopicExchange;
        this.outboundRoutingKey = outboundRoutingKey;
    }

    @RabbitListener(queues = "${messaging.message-processor.inbound-queue}", concurrency = "${messaging.message-processor.inbound-queue-concurrency}")
    public void handleMessage(@Header(X_CORRELATION_ID) UUID correlationId, final String message) {
        try {
            MDC.put(X_CORRELATION_ID, correlationId.toString());
            List<SampleMessage> sampleMessages = Arrays.asList(jsonConverter.fromJson(message, SampleMessage[].class));
            sampleMessages.forEach(sampleMessage -> {
                LOGGER.info("Received Message: " + correlationId + " " + sampleMessage.toString());
                String outboundMessage = jsonConverter.toJson(List.of(sampleMessage));
                Map<String, Object> headers = Map.of(X_CORRELATION_ID, sampleMessage.getId());
                rabbitMessagingTemplate.convertAndSend(outboundTopicExchange, outboundRoutingKey, outboundMessage, headers);
            });
        } finally {
            MDC.clear();
        }
    }
}
