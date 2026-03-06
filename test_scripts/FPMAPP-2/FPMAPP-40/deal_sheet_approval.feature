# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-40
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:29:28
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Denial of deal sheet approval by unauthorized user

  Scenario: Staff member attempts to approve a deal sheet
    Given the user is logged in as a staff member without approval rights
    When the user navigates to the deal sheet approval section
    And selects a deal sheet pending approval
    And clicks on the 'Approve' button
    Then an error message is displayed indicating insufficient permissions
    And the deal sheet status remains 'Pending'