# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-36
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:45:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email Notification for Approval Request

  Scenario: Successful email notification upon request submission
    Given the user is logged in as an approver
    When the user submits an approval request
    Then an email notification should be present in the approver's inbox
    And the email should indicate a new approval request