# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8816
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:48:51
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Exchange Rates API
  As a financial analyst
  I want to query historical currency exchange rates via API
  So that I can analyze currency trends over specified date ranges

  Background:
    Given historical currency exchange rate data exists for multiple date ranges
    And the API endpoint "/api/currency/historical" is accessible and authenticated

  Scenario: Retrieve historical currency rates for a valid date range
    When I send a GET request to "/api/currency/historical" with parameters:
      | start_date | 2026-01-01 |
      | end_date   | 2026-01-31 |
    Then the response status should be 200
    And the response should contain currency exchange rates for all dates between "2026-01-01" and "2026-01-31"
    And each record should include:
      | exchange_rate  |
      | rate_timestamp |
      | is_historical  |
      | override_flag  |
    And the "is_historical" flag should be true for all records

  Scenario: Request historical currency rates with invalid date range
    When I send a GET request to "/api/currency/historical" with parameters:
      | start_date | 2026-02-01 |
      | end_date   | 2026-01-31 |
    Then the response status should be 400
    And the response should contain an error message indicating invalid date range

  Scenario: System handles requests efficiently without timeouts or errors
    When I send a GET request to "/api/currency/historical" with parameters:
      | start_date | 2026-01-01 |
      | end_date   | 2026-01-31 |
    Then the response time should be less than 2000 milliseconds
    And the response status should be 200
