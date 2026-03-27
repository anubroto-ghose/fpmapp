# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8894
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:27:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval routing and delegation notifications
  As a requester or approver
  I want to receive real-time updates and notifications on approval status and currency syncs
  So that I can stay informed about approval routing and delegation events

  Background:
    Given the notification system is configured to send emails and in-app alerts
    And the user "approverUser" is assigned as an approver

  Scenario: User receives email and in-app notification for approval routing event
    When an approval routing event is initiated for user "approverUser" with request "Request-12345"
    Then the user "approverUser" should receive an email notification with subject "Approval Routing Notification"
    And the user "approverUser" should see an in-app notification containing "Approval Routing" and "Request-12345"

  Scenario: User receives email and in-app notification for delegation event
    When an approval delegation event is initiated from user "approverUser" to user "delegateUser" for request "Request-12345"
    Then the user "approverUser" should receive an email notification with subject "Approval Delegation Notification"
    And the user "approverUser" should see an in-app notification containing "Approval Delegation" and "Request-12345"

  Scenario: Notifications are received promptly after events
    When an approval routing event is initiated for user "approverUser" with request "Request-12345"
    And an approval delegation event is initiated from user "approverUser" to user "delegateUser" for request "Request-12345"
    Then the notifications should be received within 1 minute
