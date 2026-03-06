# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-29
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:43:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Management

  Scenario: Admin overrides incorrect currency rate
    Given the user is logged in as an admin
    When the user navigates to the currency rates management page
    And the user identifies an incorrect currency rate
    And the user uses the admin override option to correct the rate
    Then the currency rate should be successfully updated and reflect the correction made by the admin
