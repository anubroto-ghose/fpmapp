# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7226
# Epic: FPMAPP-7183
# Generated on: 2025-10-30 17:59:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Verify INR to JPY Conversion

  Scenario: Verify correct data is displayed for each INR to JPY conversion entry
    Given I am on the transaction history page
    When I look for an INR to JPY conversion entry
    Then the date should be in the correct format
    And the amount in INR should be displayed correctly
    And the amount in JPY should be correctly calculated
    And the status should be displayed as "Completed"