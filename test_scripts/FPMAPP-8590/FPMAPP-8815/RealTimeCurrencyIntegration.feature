# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8815
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:48:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Integration
  As a financial analyst
  I want the system to fetch and store real-time currency exchange rates at configured intervals
  So that I can rely on up-to-date currency data without manual intervention

  Background:
    Given the system is configured with valid third-party API credentials
    And the configured interval for fetching real-time rates is set to 5 minutes
    And the database tables for currency exchange rates are accessible

  Scenario: System fetches and stores real-time currency exchange rates periodically
    When the system starts and the currency fetch service is running
    Then the system should call the third-party API to fetch real-time currency exchange rates
    And the fetched rates should be stored in the database with correct exchange_rate, rate_timestamp, and override flags
    When the configured interval elapses again
    Then the system should call the third-party API again to fetch updated currency exchange rates
    And the newly fetched rates should be stored correctly in the database
    And no errors or failures should occur during the fetch and store process

  Scenario Outline: Verify stored currency exchange rate data correctness
    Given the system has fetched currency exchange rates for base currency "<BaseCurrency>" and target currency "<TargetCurrency>"
    When I query the database for the latest exchange rate for "<BaseCurrency>" to "<TargetCurrency>"
    Then the exchange rate should be "<ExchangeRate>"
    And the rate timestamp should be recent
    And the override flag should be false

    Examples:
      | BaseCurrency | TargetCurrency | ExchangeRate |
      | USD          | EUR            | 0.85        |
      | USD          | JPY            | 110.25      |
