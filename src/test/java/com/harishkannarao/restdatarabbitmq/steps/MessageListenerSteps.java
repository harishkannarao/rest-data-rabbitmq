package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.listener.TestMessageListener;
import com.harishkannarao.restdatarabbitmq.steps.holder.MessageListenerHolder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class MessageListenerSteps extends AbstractBaseSteps {

    private final MessageListenerHolder messageListenerHolder;

    public MessageListenerSteps(MessageListenerHolder messageListenerHolder) {
        this.messageListenerHolder = messageListenerHolder;
    }

    @Given("I clear messages in TestMessageListener")
    public void iClearMessagesInTestMessageListener() {
        TestMessageListener.HOLDER.clear();
    }

    @Given("a random sample message called {string}")
    public void aRandomSampleMessageCalled(String canonicalName) {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World" + UUID.randomUUID())
                .build();
        messageListenerHolder.getSampleMessages().put(canonicalName, sampleMessage);
    }

    @When("I send sample message {string} to message-processor.inbound-topic-exchange with message-processor.inbound-routing-key")
    public void iSendSampleMessageToMessageProcessorInboundTopicExchangeWithMessageProcessorInboundRoutingKey(String canonicalName) {
        List<SampleMessage> inputMessages = List.of(messageListenerHolder.getSampleMessages().get(canonicalName));
        String message = jsonConverter().toJson(inputMessages);
        String topicExchange = getProperty("messaging.message-processor.inbound-topic-exchange");
        String routingKey = getProperty("messaging.message-processor.inbound-routing-key");
        rabbitTemplate().convertAndSend(topicExchange, routingKey, message);
    }

    @Then("I should see sample message {string} in TestMessageListener")
    public void iShouldSeeSampleMessageInTestMessageListener(String canonicalName) {
        SampleMessage sampleMessage = messageListenerHolder.getSampleMessages().get(canonicalName);
        await().atMost(Duration.ofSeconds(5)).until(() -> TestMessageListener.HOLDER.containsKey(sampleMessage.getId()));
        var result = TestMessageListener.HOLDER.get(sampleMessage.getId());
        assertThat(result.getValue()).isEqualTo(sampleMessage.getValue());
    }
}
