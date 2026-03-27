# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8815
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:52:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Integration
  As a financial analyst
  I want the system to fetch and store real-time currency exchange rates at configured intervals
  So that I can rely on up-to-date currency data without manual intervention

  Background:
    Given the system is configured with valid third-party API credentials
    And the fetch interval is set to 5 minutes
    And the currency exchange rate database tables are accessible

  Scenario: System fetches and stores real-time currency exchange rates periodically
    When the system starts and the currency fetch service is running
    Then the system should fetch real-time currency exchange rates from the third-party API
    And the fetched rates should be stored in the database with correct exchange_rate, rate_timestamp, and override flags
    When the configured interval elapses
    Then the system should fetch the currency exchange rates again
    And the database should be updated with the new rates and timestamps
    And no errors or failures should occur during the fetch and store process

  Scenario Outline: Verify stored currency exchange rate data integrity
    Given the system has fetched currency exchange rates
    When I query the database for currency pair "<currencyPair>"
    Then the exchange_rate should be "<exchangeRate>"
    And the rate_timestamp should be recent
    And the override flag should be false

    Examples:
      | currencyPair | exchangeRate |
      | USD_EUR      | 0.85         |
      | USD_GBP      | 0.75         |
      | USD_INR      | 74.50        |
