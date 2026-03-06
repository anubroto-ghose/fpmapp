# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-29
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:26:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Management

  Scenario: Admin overrides incorrect currency rate
    Given the admin is logged in
    When the admin navigates to the currency rates management page
    And identifies an incorrect currency rate
    And uses the admin override option to correct the rate to "1.10"
    Then the currency rate should be successfully updated to "1.10"