# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-27
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:43:19
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Evaluate Currency Exchange Rate Providers

  Scenario: User evaluates currency exchange rate providers
    Given the user is logged in as a financial analyst
    When the user navigates to the currency exchange rate providers evaluation page
    Then the list of potential currency exchange rate providers is displayed
    When the user selects a provider to evaluate
    Then the provider details should be displayed correctly
