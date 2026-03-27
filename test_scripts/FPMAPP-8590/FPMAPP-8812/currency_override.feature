# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8812
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:54:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Override by Administrator
  As an administrator
  I want to override currency exchange rates with logging and alerting
  So that urgent business needs can be accommodated with audit trails

  Background:
    Given the user is logged in as an administrator
    And the currency override API endpoint is accessible

  Scenario: Successful override submission with valid reason and new rate
    When the administrator submits a currency override request with currency code "USD", new rate 1.25, and reason "Urgent business need"
    Then the API response should indicate success
    And a new override log entry should be created containing the user ID, timestamp, reason, and new rate
    And an alert should be generated for the override action
    And the currency data should show override flags and details for currency code "USD"

  Scenario Outline: Fail override submission with invalid data
    When the administrator submits a currency override request with currency code "<currencyCode>", new rate <newRate>, and reason "<reason>"
    Then the API response should indicate failure with message "<errorMessage>"

    Examples:
      | currencyCode | newRate | reason               | errorMessage                      |
      | USD          | -1.0    | Invalid negative rate| "New rate must be positive"     |
      | EUR          | 1.10    |                      | "Reason must not be empty"      |
      |             | 1.20    | Missing currency code | "Currency code is required"     |
