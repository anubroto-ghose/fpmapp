# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4617
# Epic: BANK-4579
# Generated on: 2025-07-30 05:01:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Sync of Currency Rates

  Scenario: User attempts to sync currency rates with insufficient permissions
    Given a user with insufficient permissions is logged into the system
    When the user navigates to the currency sync section in the admin dashboard
    And clicks on the 'Sync Currency Rates' button
    Then the system should deny the sync action
    And an error message should be displayed indicating insufficient permissions
