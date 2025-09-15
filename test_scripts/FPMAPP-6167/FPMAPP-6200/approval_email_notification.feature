# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6200
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:56:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email notification on approval state change
  As a requester or approver
  I want the system to send email notifications immediately when an approval changes state
  So that recipients are informed of approval activity in a timely manner

  Background:
    Given SMTP integration is configured and active
    And an approval request with id "DS-12345" exists

  Scenario: Approving an approval request triggers email notification
    When I navigate to the approval page for request "DS-12345"
    And I approve the request
    Then a POST request to "/notifications/send-email" is made with:
      | recipientEmail     | approver@example.com           |
      | subject            | Approval Status Changed for DS-12345 |
      | body               | Approval DS-12345 has been approved by user-approver-1 |
    And the email notification API responds with success and a messageId
    And the approver "approver@example.com" receives the notification email promptly
