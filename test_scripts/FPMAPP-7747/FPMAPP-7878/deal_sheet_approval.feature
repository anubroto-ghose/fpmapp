# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7878
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:21:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Deal Sheet Approval

  Scenario: Successful approval of high-value requests by directors
    Given the user is logged in as a director
    When the user navigates to the deal sheet approval section
    And selects a high-value request pending approval
    And clicks on the 'Approve' button
    Then the request is approved successfully
    And an approval log is created with user details and timestamp
    And an email notification is sent to the requester