# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-41
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:29:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Travel Request Approval

  Scenario: Approve a pending travel request
    Given the user is logged in as a travel manager
    When the user navigates to the travel request approval section
    And selects a travel request pending approval
    And clicks on the 'Approve' button
    Then the travel request status should change to 'Approved'
    And a notification should be sent to the requester confirming the approval