# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8615
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:03:39
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Automatic Email Notification on Approval Request Submission
  As an approver or requester
  I want automatic routing of approval requests with email and in-app notifications
  So that I am immediately notified when an approval request is submitted

  Background:
    Given the SMTP server configuration is active
    And the user "approverUser" is logged in as an approver
    And the approval request submission page is accessible

  Scenario: Submit approval request and receive automatic email notification
    When the user submits a new approval request with the following details:
      | projectName                    | amount  | currency |
      | Corporate Banking System Upgrade | 1500000 | USD      |
    Then an email notification is automatically sent immediately
    And the email contains correct details about the approval request
    And the notification delivery status is returned as successful by the notifications API
    And the notification event is logged for audit purposes

  Scenario Outline: Verify email notification content for various approval requests
    When the user submits a new approval request with the following details:
      | projectName | amount | currency |
      | <projectName> | <amount> | <currency> |
    Then an email notification is automatically sent immediately
    And the email contains correct details about the approval request
    And the notification delivery status is returned as successful by the notifications API
    And the notification event is logged for audit purposes

    Examples:
      | projectName                     | amount  | currency |
      | Treasury Risk Assessment       | 500000  | USD      |
      | International Loan Processing  | 2000000 | EUR      |
      | Credit Card Fraud Analysis     | 750000  | GBP      |
