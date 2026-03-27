# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8798
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:03:29
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Rates API Access
  As a financial analyst
  I want to query historical currency exchange rates
  So that I can use accurate historical data for forecasting

  Background:
    Given the historical currency rate data is populated in the system
    And the API endpoint for querying historical rates is available
    And the user has valid API credentials

  Scenario: Successfully retrieve historical currency rates for a valid currency pair and date range
    When the user sends a request to the historical currency rates API with from currency "USD" and to currency "EUR" from "2024-05-01" to "2024-05-10"
    Then the API response status code should be 200
    And the response should contain historical rates for the currency pair "USD/EUR" within the date range "2024-05-01" to "2024-05-10"
    And the response data should be formatted according to the API specification
    And the response should not contain any errors or missing data

  Scenario: Fail to retrieve historical currency rates with invalid date range
    When the user sends a request to the historical currency rates API with from currency "USD" and to currency "EUR" from "2024-06-10" to "2024-05-01"
    Then the API response status code should be 400
    And the response should contain an error message indicating invalid date range

  Scenario: Fail to retrieve historical currency rates with unauthorized access
    Given the user has invalid API credentials
    When the user sends a request to the historical currency rates API with from currency "USD" and to currency "EUR" from "2024-05-01" to "2024-05-10"
    Then the API response status code should be 401
    And the response should contain an error message indicating unauthorized access
