# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7879
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:20:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Deal Sheet Approval

  Scenario: Successful approval of mid-tier requests by managers
    Given the user is logged in as a manager
    When the user navigates to the deal sheet approval section
    And selects a mid-tier request pending approval
    And clicks on the 'Approve' button
    Then the request is approved successfully
    And an approval log is created with user details and timestamp
    And an email notification is sent to the requester
