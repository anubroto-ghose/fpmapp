# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6212
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:46:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Automatic email notifications on approval status change
  As a user involved in approval workflows
  I want automatic email notifications to be sent upon approval status changes
  So that I am informed promptly without manual checking

  Background:
    Given the SMTP email server is properly configured
    And the user "test_approver" is logged into the system
    And there is a pending approval request assigned to the user

  @EmailNotification
  Scenario: Approval status changes trigger an email notification
    When the user "test_approver" approves the pending approval request
    Then an email notification should be sent automatically
    And the email contains the updated approval status
    And the email is sent to "approver@example.com"
    And no email delivery errors are logged

  @EmailNotification
  Scenario Outline: Email content reflects correct approval statuses
    When the user "test_approver" changes the approval status to <Status>
    Then an email notification should be sent automatically
    And the email subject should contain "Approval Status Changed"
    And the email body should contain "<Status>"

    Examples:
      | Status   |
      | Approved |
      | Rejected |
      | Pending  |
