# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-39
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:29:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Deal Sheet Approval

  Scenario: Successful approval of deal sheet by authorized user
    Given the user is logged in as a financial manager with approval rights for deal sheets
    When the user navigates to the deal sheet approval section
    And selects a deal sheet pending approval
    And clicks on the 'Approve' button
    Then the deal sheet status should change to 'Approved'
    And a confirmation message should be displayed