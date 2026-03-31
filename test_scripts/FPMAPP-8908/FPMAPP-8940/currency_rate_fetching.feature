# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8940
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:50:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Rate Fetching and Storage
  As a financial analyst
  I want the system to fetch and store real-time currency exchange rates at configured intervals
  So that I can rely on up-to-date currency data for financial planning

  Background:
    Given the system is configured with a valid third-party currency API
    And the configured interval for fetching real-time rates is set to 5 seconds
    And the database is accessible and ready to store currency data

  Scenario: System fetches and stores real-time currency rates successfully
    When the currency rate fetching service is started
    And I wait for the configured interval to elapse
    Then the system should call the third-party API to fetch real-time currency rates
    And the fetched rates should be stored in the Currency Exchange Rates table with correct timestamps
    And the is_historical flag should be set to false for these entries
    And no errors or exceptions should occur during the fetch and store process

  Scenario Outline: Verify stored currency rates in the system
    Given the currency rate fetching service has fetched the latest rates
    When I query the Currency Exchange Rates table for currency code "<currencyCode>"
    Then the stored rate should be "<rate>"
    And the is_historical flag should be false
    And the rate_timestamp should be within the last 1 minute

    Examples:
      | currencyCode | rate  |
      | USD          | 1.0   |
      | EUR          | 0.85  |
      | JPY          | 110.5 |
