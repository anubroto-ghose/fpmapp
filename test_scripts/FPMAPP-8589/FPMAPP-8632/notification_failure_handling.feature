# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8632
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:52:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification failure handling and logging
  As an approver
  I want the system to handle email notification failures gracefully
  So that I still receive in-app notifications and failures are logged properly

  Background:
    Given the SMTP service is configured but simulates failure
    And an approval request is assigned to approver "approver1"

  Scenario: Email notification fails but in-app notification is sent
    When the system triggers an approval request assignment to the approver
    Then the email notification sending should fail gracefully
    And the failure details should be logged in notification_logs with status "FAILURE"
    And an in-app notification should be delivered to the approver
    And the system should not crash or lose audit trail

  Scenario: System retries or alerts on notification failure
    When the system triggers an approval request assignment to the approver
    And the email notification sending fails
    Then the system should trigger retry or alert mechanisms as per design
    And the failure should be logged with appropriate error details
    And the in-app notification fallback should be verified

  Scenario Outline: Verify notification log entries for different failure reasons
    Given the SMTP service simulates failure with message "<failureMessage>"
    When the system triggers an approval request assignment to the approver
    Then the notification_logs should contain an entry with status "FAILURE" and error details containing "<failureMessage>"

    Examples:
      | failureMessage                    |
      | SMTP server down                 |
      | Email rejected by spam filter   |
      | Connection timeout              |
