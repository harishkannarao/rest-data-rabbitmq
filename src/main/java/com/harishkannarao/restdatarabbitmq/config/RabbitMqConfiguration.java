package com.harishkannarao.restdatarabbitmq.config;

import com.harishkannarao.restdatarabbitmq.listener.MessageReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {
    @Value("${messaging.message-processor.inbound-topic-exchange}")
    private String messageProcessorInboundTopicExchange;
    @Value("${messaging.message-processor.inbound-queue}")
    private String messageProcessorInboundQueue;

    @Bean
    Queue messageProcessorInboundQueue() {
        return new Queue(messageProcessorInboundQueue, false);
    }

    @Bean
    TopicExchange messageProcessorInboundTopicExchange() {
        return new TopicExchange(messageProcessorInboundTopicExchange);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("messageProcessor");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageReceiver messageReceiver) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(messageProcessorInboundQueue);
        container.setMessageListener(new MessageListenerAdapter(messageReceiver, "processMessage"));
        return container;
    }

}
