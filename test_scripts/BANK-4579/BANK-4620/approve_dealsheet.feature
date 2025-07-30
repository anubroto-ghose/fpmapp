# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4620
# Epic: BANK-4579
# Generated on: 2025-07-30 04:59:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approve Dealsheet

  Scenario: Approve a dealsheet
    Given the approver is logged in
    When the approver accesses the list of pending dealsheets
    And selects a dealsheet from the list
    And clicks on the 'Approve' button
    Then the dealsheet status should be updated to 'Approved'
    And an audit trail entry should be created for the approval action
