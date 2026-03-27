# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8795
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:05:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Submission Validation
  As an administrator with override permissions
  I want to be prevented from submitting a currency override without providing a reason
  So that all overrides are properly logged and alerted with valid reasons

  Background:
    Given the administrator is logged into the system with override permissions
    And the CurrencyOverridePanel UI is accessible

  Scenario: Attempt override submission without providing a reason
    When the administrator navigates to the CurrencyOverridePanel
    And enters a valid currency pair "USD/EUR"
    And enters a new exchange rate "0.85"
    And leaves the reason field empty
    And attempts to submit the override
    Then the system prevents submission
    And displays a validation error indicating that the reason is mandatory
    And no override is logged in the Currency_Override_Logs table
    And no alert is triggered
    And the UI provides real-time feedback about the missing reason
