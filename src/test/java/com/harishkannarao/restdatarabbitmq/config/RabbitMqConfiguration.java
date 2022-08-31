package com.harishkannarao.restdatarabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RabbitMqConfiguration {

    @Bean(name = "messageProcessorInboundQueue")
    Queue messageProcessorInboundQueue(@Value("${messaging.message-processor.inbound-queue}") String messageProcessorInboundQueue) {
        return new Queue(messageProcessorInboundQueue, false);
    }

    @Bean(name = "messageProcessorInboundTopicExchange")
    TopicExchange messageProcessorInboundTopicExchange(@Value("${messaging.message-processor.inbound-topic-exchange}") String messageProcessorInboundTopicExchange) {
        return new TopicExchange(messageProcessorInboundTopicExchange);
    }

    @Bean
    Binding inboundBinding(@Qualifier("messageProcessorInboundQueue") Queue queue,
                    @Qualifier("messageProcessorInboundTopicExchange") TopicExchange exchange,
                    @Value("${messaging.message-processor.inbound-routing-key}") String messageProcessorInboundRoutingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(messageProcessorInboundRoutingKey);
    }

    @Bean(name = "messageProcessorOutboundQueue")
    Queue messageProcessorOutboundQueue(@Value("${messaging.message-processor.outbound-queue}") String messageProcessorOutboundQueue) {
        return new Queue(messageProcessorOutboundQueue, false);
    }

    @Bean(name = "messageProcessorOutboundTopicExchange")
    TopicExchange messageProcessorOutboundTopicExchange(@Value("${messaging.message-processor.outbound-topic-exchange}") String messageProcessorOutboundTopicExchange) {
        return new TopicExchange(messageProcessorOutboundTopicExchange);
    }

    @Bean
    Binding outboundBinding(@Qualifier("messageProcessorOutboundQueue") Queue queue,
                           @Qualifier("messageProcessorOutboundTopicExchange") TopicExchange exchange,
                           @Value("${messaging.message-processor.outbound-routing-key}") String messageProcessorOutboundRoutingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(messageProcessorOutboundRoutingKey);
    }

}
