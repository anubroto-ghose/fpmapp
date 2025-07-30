# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4671
# Epic: BANK-4644
# Generated on: 2025-07-30 16:58:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Prevent direct saving of financial entries

  Scenario: User attempts to save financial entry without approval
    Given the user is logged in as a financial analyst
    When the user navigates to the financial entry form
    And fills in the required fields with valid data
    And attempts to save the financial entry directly
    Then the system should prevent the user from saving the financial entry
    And display an error message indicating that approval is required