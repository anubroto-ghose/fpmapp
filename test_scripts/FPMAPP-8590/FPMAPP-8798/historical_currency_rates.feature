# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8798
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:34:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Rates API Access
  As a financial analyst
  I want to query historical currency exchange rates via API
  So that I can use accurate data for forecasting

  Background:
    Given historical currency rate data is populated in the system
    And the API endpoint for querying historical rates is available
    And the user has valid API credentials

  Scenario: Query historical currency rates for a specific currency pair and date range
    When the user sends a request to the historical currency rates API with currency pair "USD/EUR" and date range from "2026-03-20" to "2026-03-22"
    Then the API response status code should be 200
    And the response should contain historical currency rates for "USD/EUR" within the date range "2026-03-20" to "2026-03-22"
    And the response data should be correctly formatted according to the API specification
    And the response should contain no errors or missing data
