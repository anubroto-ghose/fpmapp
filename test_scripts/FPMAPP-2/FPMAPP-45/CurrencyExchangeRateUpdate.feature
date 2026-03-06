# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-45
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:48:36
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Update

  Scenario: Verify automatic currency exchange rate updates at defined intervals
    Given the system is configured to fetch exchange rates from the API at set intervals
    When I wait for the defined interval to pass
    Then the exchange rates in the database should reflect the latest rates fetched from the API
