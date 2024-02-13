package com.harishkannarao.restdatarabbitmq.steps;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.listener.TestMessageListener;
import com.harishkannarao.restdatarabbitmq.logback.LogbackTestAppender;
import com.harishkannarao.restdatarabbitmq.steps.holder.LogbackAppenderHolder;
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
    private final LogbackAppenderHolder logbackAppenderHolder;

    public MessageListenerSteps(
            MessageListenerHolder messageListenerHolder,
            LogbackAppenderHolder logbackAppenderHolder) {
        this.messageListenerHolder = messageListenerHolder;
        this.logbackAppenderHolder = logbackAppenderHolder;
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

    @Then("I should see log message {string} at least {int} times for message {string}")
    public void iShouldSeeLogMessage(String message, Integer atLeast, String canonicalName) {
        SampleMessage sampleMessage = messageListenerHolder.getSampleMessages().get(canonicalName);
        LogbackTestAppender messageListenerAppender = logbackAppenderHolder.getMessageListenerAppender();
        await().atMost(Duration.ofSeconds(25))
                .untilAsserted(() -> {
                    List<String> filteredLogs = messageListenerAppender.getLogs().stream()
                            .filter(iLoggingEvent -> {
                                String correlationId = iLoggingEvent.getMDCPropertyMap().get("X-Correlation-ID");
                                return correlationId.equals(sampleMessage.getId().toString());
                            })
                            .map(ILoggingEvent::getFormattedMessage)
                            .filter(s -> s.contains(message))
                            .toList();
                    assertThat(filteredLogs)
                            .hasSizeGreaterThanOrEqualTo(atLeast);
                });
    }
}
