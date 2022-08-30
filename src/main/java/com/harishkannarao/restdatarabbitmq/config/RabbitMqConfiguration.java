package com.harishkannarao.restdatarabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
    Binding binding(@Qualifier("messageProcessorInboundQueue") Queue queue, @Qualifier("messageProcessorInboundTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("messageProcessor");
    }

}
