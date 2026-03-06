# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-36
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:28:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Email Notification

  Scenario: Successful email notification upon request submission
    Given the user is logged in as an approver
    When the user submits an approval request
    Then an email notification should be sent to the approver's inbox
    And the email should indicate a new approval request