# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5266
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:41:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Retrieve Historical Currency Exchange Rates
  As a logged-in user
  I want to access historical currency exchange rates for a specified date range
  So that I can review past currency fluctuations accurately

  Background:
    Given the user is logged in with username "testuser" and password "password123"
    And the currency exchange API connection is available

  Scenario: Successfully retrieve and display historical currency rates
    When the user navigates to the historical currency data request page
    And the user enters currency code "USD"
    And the user selects start date "2025-08-01" and end date "2025-08-03"
    And the user submits the historical data request
    Then the system fetches historical rates for currency code "USD" between "2025-08-01" and "2025-08-03"
    And the system displays the historical currency rates in a table
    And each displayed rate has a date within the specified range
    And the displayed currency code for all rates is "USD"

  Scenario Outline: Reject invalid currency code input
    When the user navigates to the historical currency data request page
    And the user enters currency code "<currencyCode>"
    And the user selects start date "2025-08-01" and end date "2025-08-03"
    And the user submits the historical data request
    Then the system displays an error message "<errorMessage>"

    Examples:
      | currencyCode | errorMessage                     |
      | ABC123       | "Invalid currency code provided" |
      | ""          | "Currency code cannot be empty" |
      | 123          | "Invalid currency code provided" |
