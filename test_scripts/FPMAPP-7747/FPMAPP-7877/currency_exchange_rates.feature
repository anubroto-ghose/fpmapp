# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7877
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:22:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Querying Historical Exchange Rates

  Scenario: Retrieve historical exchange rates for a specific date
    Given the historical exchange rates are stored in the CurrencyExchangeRates table
    When I send a GET request to "/api/currency/exchange-rates/history" with the date "2023-01-01"
    Then the response should return historical exchange rates for the specified date
    And the response should include "currency_code" and "exchange_rate" fields