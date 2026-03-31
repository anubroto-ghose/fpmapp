# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8941
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:49:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Query Historical Currency Rates
  As a financial analyst
  I want to query historical currency exchange rates by date/time
  So that I can analyze past financial data accurately

  Background:
    Given historical currency rates data exists in the database with correct timestamps and is_historical flag set to true
    And the API endpoint for querying historical rates is accessible

  Scenario: Successfully query historical currency rates for a specific date/time
    When I send a request to the historical currency rates API with base currency "USD", target currency "EUR", and date/time "2023-03-15T10:30:00Z"
    Then the API response status should be 200
    And the response should include the rate_timestamp "2023-03-15T10:30:00Z"
    And the is_historical flag should be true
    And the response should include the correct currency rate
    And the override status and timestamps should be included if overrides exist
    And no errors or incorrect data should be returned

  Scenario: Query historical currency rates with override data
    Given the historical currency rate has an override with status "OVERRIDDEN" and override timestamp "2023-03-16T08:00:00Z"
    When I send a request to the historical currency rates API with base currency "USD", target currency "GBP", and date/time "2023-03-14T09:00:00Z"
    Then the API response status should be 200
    And the response should include the rate_timestamp "2023-03-14T09:00:00Z"
    And the is_historical flag should be true
    And the override status should be "OVERRIDDEN"
    And the override timestamp should be "2023-03-16T08:00:00Z"
    And no errors or incorrect data should be returned

  Scenario: Query historical currency rates for a date/time with no data
    When I send a request to the historical currency rates API with base currency "USD", target currency "JPY", and date/time "2000-01-01T00:00:00Z"
    Then the API response status should be 404
    And the response should indicate no historical data found
