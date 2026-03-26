# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8799
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:35:36
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Anomaly Alerting
  As a financial analyst
  I want the system to detect anomalies in real-time currency exchange rates
  So that I receive prompt alerts with relevant details

  Background:
    Given the alerting system is configured and active
    And currency rate anomaly detection rules are defined
    And the system is connected to the third-party currency API

  Scenario: Detect and alert on sudden spike in currency rate
    When an anomalous currency rate "USD/EUR" with value "1.50" is injected
    And the system processes the incoming currency data
    Then the system should detect the anomaly for "USD/EUR"
    And an alert should be triggered promptly
    And alert notifications should be sent to configured recipients
    And the alert notification should contain details about the anomaly
    And no false positives or missed alerts should occur during the test

  Scenario: No alert on normal currency rate
    When a normal currency rate "USD/EUR" with value "0.85" is injected
    And the system processes the incoming currency data
    Then no alert should be triggered for "USD/EUR"

  Scenario: Alert notification contains relevant details
    When an anomalous currency rate "USD/EUR" with value "1.50" is injected
    And the system processes the incoming currency data
    Then the alert notification for "USD/EUR" should include the currency pair
    And the alert notification should describe the anomaly
    And the alert notification should include the numeric rate value
