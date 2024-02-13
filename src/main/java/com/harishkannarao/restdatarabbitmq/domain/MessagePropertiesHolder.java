package com.harishkannarao.restdatarabbitmq.domain;

import java.time.Duration;

public record MessagePropertiesHolder(
        String outboundTopicExchange,
        String outboundRoutingKey,
        String inboundTopicExchange,
        String inboundRoutingKey,
        String inboundRetryTopicExchange,
        String inboundRetryRoutingKey,
        Duration nextRetryDuration,
        Duration msgExpiryDuration,
        String multiplicationFactor
) {
}
