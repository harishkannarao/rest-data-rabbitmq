package com.harishkannarao.restdatarabbitmq.config;

import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;

@Configuration
public class PropertiesConfiguration {

    @Bean
    public MessagePropertiesHolder messagePropertiesHolder(final Environment env) {
        return new MessagePropertiesHolder(
                env.getRequiredProperty("messaging.message-processor.outbound-topic-exchange"),
                env.getRequiredProperty("messaging.message-processor.outbound-routing-key"),
                env.getRequiredProperty("messaging.message-processor.inbound-topic-exchange"),
                env.getRequiredProperty("messaging.message-processor.inbound-routing-key"),
                env.getRequiredProperty("messaging.message-processor.inbound-retry-topic-exchange"),
                env.getRequiredProperty("messaging.message-processor.inbound-retry-routing-key"),
                env.getRequiredProperty("messaging.message-processor.inbound-retry-delay-duration", Duration.class),
                env.getRequiredProperty("messaging.message-processor.inbound-retry-message-expiry-duration", Duration.class),
                env.getRequiredProperty("messaging.message-processor.inbound-retry-delay-multiplication-factor")
        );
    }
}
