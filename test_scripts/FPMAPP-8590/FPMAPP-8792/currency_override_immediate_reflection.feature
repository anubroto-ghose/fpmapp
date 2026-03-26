# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8792
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:02:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Immediate Currency Rate Override Reflection in UI
  As a user of the FPMApplication
  I want the currency rate override to be reflected immediately in the UI
  So that I get real-time feedback without refreshing the page

  Background:
    Given the user "testuser" is logged into the FPMApplication UI
    And the CurrencyOverridePanel component is visible and active
    And a WebSocket connection for real-time currency updates is established

  Scenario: Currency rate override updates immediately in the UI
    When a currency rate override for "USD/EUR" with new rate "1.2345" is performed by another user
    Then the currency rate displayed in the CurrencyOverridePanel updates immediately to "1.2345"
    And the CurrencyOverridePanel shows the validation message "Override successful"
    And the page does not reload
    And no error notifications are shown
    And the UI remains responsive
