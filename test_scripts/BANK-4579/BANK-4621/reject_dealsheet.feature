# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4621
# Epic: BANK-4579
# Generated on: 2025-07-30 04:58:39
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Reject a Dealsheet

  Scenario: Approver rejects a pending dealsheet
    Given the approver is logged in
    And there are pending dealsheets available
    When the approver accesses the list of pending dealsheets
    And selects a dealsheet from the list
    And clicks on the 'Reject' button
    Then the dealsheet status should be updated to 'Rejected'
    And an audit trail entry should be created for the rejection action
