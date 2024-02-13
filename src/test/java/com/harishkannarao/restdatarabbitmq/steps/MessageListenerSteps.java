package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.listener.TestMessageListener;
import com.harishkannarao.restdatarabbitmq.steps.holder.MessageListenerHolder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;
import java.util.Map;
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

    @Given("a random sample message called {string} with value {string}")
    public void aRandomSampleMessageWithValue(String canonicalName, String value) {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value(value)
                .build();
        messageListenerHolder.getSampleMessages().put(canonicalName, sampleMessage);
    }

    @When("I send sample message {string} to {string} with {string}")
    public void iSendSampleMessageToMessageProcessorInboundTopicExchangeWithMessageProcessorInboundRoutingKey(
            String canonicalName,
            String inboundTopicExchange,
            String inboundRoutingKey) {
        SampleMessage sampleMessage = messageListenerHolder.getSampleMessages().get(canonicalName);
        List<SampleMessage> inputMessages = List.of(sampleMessage);
        String message = jsonConverter().toJson(inputMessages);
        String topicExchange = getProperty(inboundTopicExchange);
        String routingKey = getProperty(inboundRoutingKey);
        Map<String, Object> headers = Map.of("X-Correlation-ID", sampleMessage.getId());
        rabbitMessagingTemplate().convertAndSend(topicExchange, routingKey, message, headers);
    }

    @Then("I should see sample message {string} in TestMessageListener")
    public void iShouldSeeSampleMessageInTestMessageListener(String canonicalName) {
        SampleMessage sampleMessage = messageListenerHolder.getSampleMessages().get(canonicalName);
        await().atMost(Duration.ofSeconds(20)).until(() -> TestMessageListener.HOLDER.containsKey(sampleMessage.getId()));
        var result = TestMessageListener.HOLDER.get(sampleMessage.getId());
        assertThat(result.getValue()).isEqualTo(sampleMessage.getValue());
    }
}
