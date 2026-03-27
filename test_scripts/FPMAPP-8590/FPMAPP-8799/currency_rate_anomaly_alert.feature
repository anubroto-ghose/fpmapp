# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8799
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:02:58
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Anomaly Alerting
  As a financial analyst
  I want the system to detect and alert on anomalous currency exchange rate data
  So that I can be promptly notified of potential financial risks

  Background:
    Given the alerting system is configured and active
    And currency rate anomaly detection rules are defined
    And the system is connected to the third-party currency API

  Scenario: Alert triggers on sudden spike in currency exchange rate
    When an anomalous currency rate data with a sudden spike is injected for "USD" to "EUR"
    And the system processes the incoming currency rate data
    Then the system should detect the currency rate anomaly
    And an alert should be triggered promptly
    And the alert notification should contain details about the anomaly for "USD" to "EUR"
    And the alert notification should be sent to the configured recipients
    And no false positive alerts should be generated

  Scenario: Alert triggers on sudden drop in currency exchange rate
    When an anomalous currency rate data with a sudden drop is injected for "USD" to "GBP"
    And the system processes the incoming currency rate data
    Then the system should detect the currency rate anomaly
    And an alert should be triggered promptly
    And the alert notification should contain details about the anomaly for "USD" to "GBP"
    And the alert notification should be sent to the configured recipients
    And no false positive alerts should be generated
