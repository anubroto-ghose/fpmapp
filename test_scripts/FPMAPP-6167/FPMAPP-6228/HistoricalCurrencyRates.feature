# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6228
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:34:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Rates Retrieval

  As a financial admin user
  I want to retrieve historical currency rates through an API endpoint
  So that I can review currency rate history for valid date ranges

  Background:
    Given historical currency rate data exists for currency "USD" from "2025-01-01" to "2025-01-10"
    And the API endpoint "/currency/rates/historical" is available

  Scenario: Retrieve historical currency rates with valid date range
    When I send a GET request to "/currency/rates/historical" with parameters:
      | currencyCode | USD          |
      | startDate    | 2025-01-01   |
      | endDate      | 2025-01-10   |
    Then the response status code should be 200
    And the response contains a rates array
    And each entry in the rates array has a valid date string and a positive rate number
    And all dates in the rates array are within the range from "2025-01-01" to "2025-01-10"
    And no entries contain the field "override_flag"
