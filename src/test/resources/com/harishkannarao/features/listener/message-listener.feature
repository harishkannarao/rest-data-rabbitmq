Feature: message-listener

  Scenario: message-listener receives and sends message
    Given I clear messages in TestMessageListener
    Given a random sample message called "sample_message"
    When I send sample message "sample_message" to message-processor.inbound-topic-exchange with message-processor.inbound-routing-key
    Then I should see sample message "sample_message" in TestMessageListener