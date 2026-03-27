# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8625
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:56:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rates API
  As a financial analyst
  I want to fetch current and historical currency exchange rates
  So that I can analyze financial data accurately

  Background:
    Given the currency rates data exists in the database including historical entries with timestamps
    And the API endpoint GET /fpm/currency/rates is accessible

  Scenario: Fetch current currency rate without timestamp
    When I call the API with currency code "USD" without timestamp
    Then the API returns the latest currency rate
    And the response contains fields rate, timestamp, and override_flag
    And the override_flag is false

  Scenario: Fetch historical currency rate with valid timestamp
    Given a historical timestamp "2024-05-01T10:00:00Z" exists for currency code "EUR"
    When I call the API with currency code "EUR" and timestamp "2024-05-01T10:00:00Z"
    Then the API returns the historical currency rate for the given timestamp
    And the response contains fields rate, timestamp, and override_flag
    And the override_flag is true

  Scenario: Fetch currency rate with future timestamp
    When I call the API with currency code "USD" and a future timestamp "2099-01-01T00:00:00Z"
    Then the API returns an error indicating no data for future timestamp

  Scenario: Fetch currency rate with invalid currency code
    When I call the API with invalid currency code "XYZ"
    Then the API returns an error indicating invalid currency code
