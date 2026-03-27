# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8610
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:07:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate API Integration
  As a financial analyst
  I want to fetch current and historical currency exchange rates
  So that I can perform accurate financial analysis with real-time and historical data

  Background:
    Given the currency exchange rates have been synchronized and stored with historical versions
    And the API endpoint "/fpm/currency/rates" is accessible

  Scenario: Fetch latest exchange rate without timestamp
    When I send a GET request to "/fpm/currency/rates" with currency_pair "USD_EUR" and no timestamp
    Then the response status should be 200
    And the response should contain the latest exchange rate for "USD_EUR"
    And the response format should conform to the API specification

  Scenario: Fetch historical exchange rate with valid timestamp
    Given a historical timestamp "2023-01-01T12:00:00Z"
    When I send a GET request to "/fpm/currency/rates" with currency_pair "USD_EUR" and timestamp "2023-01-01T12:00:00Z"
    Then the response status should be 200
    And the response should contain the exchange rate corresponding to the timestamp "2023-01-01T12:00:00Z"
    And the response format should conform to the API specification
