# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8794
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:32:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Administration
  As an administrator
  I want to override currency exchange rates with valid reasons
  So that all overrides are logged and alert notifications are sent

  Background:
    Given an administrator is logged into the system with override permissions
    And the Currency Override Panel is accessible

  Scenario: Submit currency override with valid reason and verify logging and alerting
    When the administrator navigates to the Currency Override Panel
    And enters a valid currency pair "USD/EUR" and new exchange rate "0.85"
    And provides a mandatory reason "Quarterly adjustment due to market volatility"
    And submits the override
    Then the override submission should succeed with real-time feedback
    And the override should be logged with admin user details, timestamp, override values, and reason
    And an alert notification should be sent to the relevant stakeholders
    And the override should be retrievable via the API with correct data
