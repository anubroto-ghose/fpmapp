# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8816
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:51:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Exchange Rates API
  As a financial analyst
  I want to query historical currency exchange rates for specified date ranges
  So that I can analyze past currency trends accurately

  Background:
    Given the historical currency exchange rate data exists in the system
    And the API endpoint "/api/currency/historical" is accessible and authenticated

  Scenario: Query historical currency rates with a valid date range
    When I send a GET request to "/api/currency/historical" with start_date "2026-01-01" and end_date "2026-01-31"
    Then the response status should be 200
    And the response should contain currency exchange rates for all dates from "2026-01-01" to "2026-01-31"
    And each record should include exchange_rate, rate_timestamp, is_historical flag set to true, and override flags

  Scenario: Query historical currency rates with an invalid date range
    When I send a GET request to "/api/currency/historical" with start_date "2026-02-01" and end_date "2026-01-01"
    Then the response status should be 400
    And the response should contain an error message indicating invalid date range
