# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8794
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:05:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Submission by Administrator
  As an administrator with override permissions
  I want to submit currency exchange rate overrides with valid reasons
  So that all overrides are logged and alert notifications are sent

  Background:
    Given an administrator is logged into the system with override permissions
    And the CurrencyOverridePanel UI is accessible

  Scenario: Submit a valid currency override and verify logging and alerting
    When the administrator navigates to the CurrencyOverridePanel
    And enters "USD" as the from currency
    And enters "EUR" as the to currency
    And enters "1.15" as the new exchange rate
    And provides "Admin override for testing" as the reason for override
    And submits the override
    Then the override submission should succeed with a confirmation message
    And the override should be logged with the administrator's details, timestamp, override values, and reason
    And an alert notification should be sent to the relevant stakeholders
    And the override should be retrievable via the API with correct data

  Scenario Outline: Submit override with missing mandatory reason
    When the administrator navigates to the CurrencyOverridePanel
    And enters "<fromCurrency>" as the from currency
    And enters "<toCurrency>" as the to currency
    And enters "<exchangeRate>" as the new exchange rate
    And provides "" as the reason for override
    And attempts to submit the override
    Then the submission should be rejected with an error message "Reason for override is mandatory"

    Examples:
      | fromCurrency | toCurrency | exchangeRate |
      | USD          | EUR        | 1.15        |
      | GBP          | USD        | 1.30        |
