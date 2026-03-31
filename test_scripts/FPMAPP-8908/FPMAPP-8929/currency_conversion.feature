# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8929
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:00:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Conversion API Integration
  As a financial analyst
  I want to query current and historical currency exchange rates
  So that I can access real-time and historical financial data accurately

  Background:
    Given the currency exchange rates data is available in the system
    And the user has valid API access credentials

  Scenario: Query current currency exchange rates successfully
    When the user sends a request to query current currency exchange rates
    Then the API returns up-to-date exchange rates
    And the response contains rates for USD, EUR, and JPY
    And the response status code is 200

  Scenario: Query historical currency exchange rates for a valid date range
    Given the date range from "2023-01-01" to "2023-01-05"
    When the user sends a request to query historical currency exchange rates for the date range
    Then the API returns accurate historical data matching the requested dates
    And the response contains rates for USD, EUR, and JPY for each date
    And the response status code is 200

  Scenario: Query historical currency exchange rates with invalid date range
    Given the invalid date range from "2023-01-10" to "2023-01-05"
    When the user sends a request to query historical currency exchange rates for the date range
    Then the API returns an error message indicating invalid date range
    And the response status code is 400

  Scenario: Query historical currency exchange rates with malformed request
    Given the date range with missing end date "2023-01-01"
    When the user sends a malformed request to query historical currency exchange rates
    Then the API returns an error message indicating malformed request
    And the response status code is 400
