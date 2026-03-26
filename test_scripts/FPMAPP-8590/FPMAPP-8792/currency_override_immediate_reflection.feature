# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8792
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:30:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Immediate Currency Rate Override Reflection in UI
  As a user of the FPMApplication
  I want to see currency rate overrides reflected immediately in the UI
  So that I get real-time feedback without refreshing the page

  Background:
    Given the user is logged into the FPMApplication UI
    And the CurrencyOverridePanel component is visible and active
    And a WebSocket or real-time sync connection is established

  Scenario: Currency rate override updates immediately in the UI
    When a currency rate override is performed by another user or system
    Then the overridden currency rate should update immediately in the UI
    And the CurrencyOverridePanel should reflect the new rate and any validation messages
    And no full page reload should occur
    And the UI should remain responsive with no error notifications
