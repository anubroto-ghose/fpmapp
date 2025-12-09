# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7875
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:23:43
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Fetching

  Scenario: Successful fetching of current exchange rates from API
    Given the system is connected to the third-party currency exchange API
    When I send a GET request to "/api/currency/exchange-rates"
    Then the response should return a JSON object containing "currency_code" and "exchange_rate" fields
    And the response status should be 200 OK
