# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-28
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:26:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Auto-sync currency rates

  Scenario: User sets auto-sync interval and checks currency rates
    Given the user is logged in
    And the user has access to the auto-sync settings
    When the user sets the auto-sync interval to 30 minutes
    And waits for the sync to occur
    Then the currency rates should be updated automatically at the defined interval
