# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4614
# Epic: BANK-4579
# Generated on: 2025-07-30 05:04:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: View Historical Currency Rates

  Scenario: Handling of invalid criteria for historical currency rates
    Given the user is logged in with appropriate permissions
    When the user navigates to the historical currency rates section
    And the user specifies invalid criteria for retrieving historical rates
    Then the system should display an error message indicating that the criteria provided are invalid.