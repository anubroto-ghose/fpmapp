# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7876
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:22:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Auto-sync of exchange rates

  Scenario: Verify auto-sync of exchange rates every 10 minutes
    Given the system is set to auto-sync exchange rates every 10 minutes
    When I wait for 10 minutes after the initial fetch of exchange rates
    Then the CurrencyExchangeRates table should reflect the latest exchange rates fetched from the API after the sync interval