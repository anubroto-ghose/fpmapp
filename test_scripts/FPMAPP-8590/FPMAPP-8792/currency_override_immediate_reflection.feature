# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8792
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:15:13
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Immediate Currency Rate Override Reflection in UI
  As a user of the FPMApplication
  I want to see currency rate overrides reflected immediately in the UI
  So that I do not need to refresh the page and can trust the displayed data

  Background:
    Given the user is logged into the FPMApplication UI
    And the CurrencyOverridePanel component is visible and active
    And a WebSocket or real-time sync connection is established

  Scenario: Currency rate override updates immediately in the UI without page refresh
    When a currency rate override is performed by another user or system for currency "USD" with new rate 1.25
    Then the overridden currency rate "1.25" is displayed immediately in the CurrencyOverridePanel
    And the CurrencyOverridePanel reflects any validation messages if present
    And the page does not reload
    And the UI remains responsive
    And no error notifications are shown
