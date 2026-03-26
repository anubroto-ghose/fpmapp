# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8812
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:46:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Override Management
  As an administrator
  I want to override currency exchange rates with logging and alerting
  So that financial data remains accurate and auditable

  Background:
    Given the user is logged in with administrator role
    And the currency override API endpoint is accessible

  Scenario: Successful override submission with valid reason and new rate
    When the administrator submits a currency override request with:
      | currencyPair | USD/EUR                             |
      | newRate      | 1.2345                             |
      | reason       | Quarterly adjustment due to market volatility |
    Then the API response should indicate success
    And the override log entry should be created with:
      | performedByUserId | adminUserId                    |
      | reason            | Quarterly adjustment due to market volatility |
      | newRate           | 1.2345                         |
    And an override alert should be generated
    And the currency data queried via UI should display override flags and details

  Scenario Outline: Override submission fails with invalid input
    When the administrator submits a currency override request with:
      | currencyPair | <currencyPair> |
      | newRate      | <newRate>      |
      | reason       | <reason>       |
    Then the API response should indicate failure with message "<errorMessage>"

    Examples:
      | currencyPair | newRate | reason | errorMessage                         |
      | USD/EUR     | -1.0    | Valid reason | Exchange rate must be positive       |
      | USD/EUR     | 1.2     |          | Reason must not be empty             |
      |             | 1.2     | Valid reason | Currency pair must be provided      |
