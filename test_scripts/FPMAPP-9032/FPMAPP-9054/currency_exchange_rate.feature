# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9054
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:46:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate API
  As a finance admin
  I want to query current and historical currency exchange rates
  So that I can synchronize and override currency rates accurately

  Background:
    Given the currency exchange rates have been synchronized
    And historical data exists in the database

  Scenario: Query latest exchange rate without timestamp
    When I send a GET request to "/api/fpm/currency/rates" with currency_pair "USD_EUR" and no timestamp
    Then the response status should be 200
    And the response should contain the latest exchange rate for "USD_EUR"
    And the response time should be within acceptable limits

  Scenario: Query historical exchange rate with valid timestamp
    Given a historical timestamp "2023-01-01T00:00:00Z"
    When I send a GET request to "/api/fpm/currency/rates" with currency_pair "USD_EUR" and timestamp "2023-01-01T00:00:00Z"
    Then the response status should be 200
    And the response should contain the exchange rate for "USD_EUR" at timestamp "2023-01-01T00:00:00Z"
    And the response time should be within acceptable limits

  Scenario: Query exchange rate with invalid future timestamp
    Given a future timestamp "2099-12-31T23:59:59Z"
    When I send a GET request to "/api/fpm/currency/rates" with currency_pair "USD_EUR" and timestamp "2099-12-31T23:59:59Z"
    Then the response status should be 400
    And the response should contain an appropriate error message
    And the response time should be within acceptable limits
