# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6231
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:32:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email notification dispatch on approval state changes
  As a requester or approver
  I want approval requests to be automatically routed with timely email and in-app notifications
  So that I receive immediate notification of approval status updates including approval, rejection, delegation, and override

  Background:
    Given the SMTP integration is configured and active
    And user email addresses are correctly registered in the system
    And the approval workflow can be initiated and progressed

  Scenario Outline: Email notification trigger on approval state changes
    When I initiate an approval request with request id "<requestId>" from requester "<requesterEmail>" to approver "<approverEmail>"
    And the approval state is changed to "<state>"
    Then an email notification should be triggered immediately
    And the email should be sent to "<approverEmail>"
    And the email subject should contain "<state>" notification
    And the email body should mention the request id "<requestId>" and state "<state>"
    And the notification API should respond with success and messageId
    And no email delivery delays beyond acceptable thresholds are detected

    Examples:
      | requestId | requesterEmail           | approverEmail           | state     |
      | REQ123    | requester@example.com    | approver@example.com    | approved  |
      | REQ123    | requester@example.com    | approver@example.com    | rejected  |
      | REQ123    | requester@example.com    | approver@example.com    | delegated |
      | REQ123    | requester@example.com    | approver@example.com    | overridden|
