# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8630
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:53:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Email Notification
  As an approver
  I want to receive automatic email notifications when an approval request is assigned to me
  So that I can promptly take action on approval requests

  Background:
    Given the SMTP email service is configured and operational
    And an approval request exists

  Scenario: Email notification is sent on approval request assignment
    Given an approval request is assigned to a valid approver with email "approver@example.com"
    When the approval request assignment is triggered
    Then an email notification should be sent to "approver@example.com"
    And the email content should include the approval details and delegation information
    And the notification event should be logged in the notification_logs database table with status "SENT"

  Scenario Outline: Email notification content validation
    Given an approval request is assigned to a valid approver with email "<approverEmail>"
    When the approval request assignment is triggered
    Then an email notification should be sent to "<approverEmail>"
    And the email content should include "<expectedContent>"
    And the notification event should be logged in the notification_logs database table with status "SENT"

    Examples:
      | approverEmail          | expectedContent                      |
      | approver@example.com   | Request approval for project budget |
      | delegate@example.com   | delegation                         |
