# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8810
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:44:37
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time in-app notification for approval delegation
  As a user with delegation permissions
  I want to receive immediate, actionable in-app notifications when an approval request is delegated to me
  So that I can promptly view and act on delegated approvals

  Background:
    Given the user "delegatedUser" is logged into the FPMApplication
    And the user has delegation permissions
    And the WebSocket connection is active for user "delegatedUser"

  Scenario: Receive and interact with in-app notification upon approval delegation
    Given another user "managerUser" has delegated approval request "approval-12345" to user "delegatedUser"
    When the user "delegatedUser" views the application dashboard
    Then the user should receive an in-app notification about the delegation
    And the notification should contain the message "You have been delegated an approval request."
    And the notification should indicate it is from "managerUser"
    When the user clicks on the notification action to view the approval
    Then the user should be navigated to the approval detail page for "approval-12345"
