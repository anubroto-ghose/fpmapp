# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8792
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:07:09
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Immediate Currency Rate Override Reflection in UI
  As a financial user of FPM Tools
  I want to see immediate feedback on currency rate overrides
  So that I can trust the UI reflects real-time financial data without page refresh

  Background:
    Given the user "testuser" is logged into the FPM Tools UI
    And the CurrencyOverridePanel component is visible and active
    And a WebSocket connection for currency updates is established

  Scenario: Currency rate override updates immediately in the UI
    When a currency rate override is performed for "USD" to "EUR" with rate "0.85" from another user or system
    Then the currency rate displayed in the UI updates immediately to "0.85"
    And the CurrencyOverridePanel shows the validation message "Rate override successful"
    And the page does not perform a full reload
    And the UI remains responsive with no error notifications
