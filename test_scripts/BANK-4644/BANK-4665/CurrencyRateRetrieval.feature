# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4665
# Epic: BANK-4644
# Generated on: 2025-07-30 17:00:05
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Retrieve Historical Currency Rates

  Scenario: User retrieves historical currency rates based on transaction date
    Given historical currency rates are stored in the ExchangeRateHistory table
    When the user queries the system for historical currency rates on "2023-10-01" for currency "USD"
    Then the system should return the correct historical currency rate of "1.2"