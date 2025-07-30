# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4619
# Epic: BANK-4579
# Generated on: 2025-07-30 05:00:11
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Access Pending Dealsheets

  Scenario: Approver accesses pending dealsheets
    Given the approver is logged into the system
    When the approver navigates to the dealsheets section
    And clicks on the 'Pending Dealsheets' tab
    Then the system displays a list of all pending dealsheets for the approver
