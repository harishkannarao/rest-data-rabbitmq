Feature: message-listener

  Scenario: message-listener receives inbound message and sends outbound message
    Given I clear messages in TestMessageListener
    Given a random sample message called "sample_message"
    When I send sample message "sample_message" to "messaging.message-processor.inbound-topic-exchange" with "messaging.message-processor.inbound-routing-key"
    Then I should see sample message "sample_message" in TestMessageListener

  Scenario: message-listener receives inbound message then retries and sends outbound message
    Given I clear messages in TestMessageListener
    Given a random sample message called "sample_message" with value "$$"
    When I send sample message "sample_message" to "messaging.message-processor.inbound-topic-exchange" with "messaging.message-processor.inbound-routing-key"
    Then I should see sample message "sample_message" in TestMessageListener