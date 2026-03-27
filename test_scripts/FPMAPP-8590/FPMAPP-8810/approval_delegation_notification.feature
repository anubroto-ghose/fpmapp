# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8810
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:55:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: In-app notification for approval delegation event
  As a user with delegation permissions
  I want to receive immediate, real-time feedback when an approval is delegated to me
  So that I can act on delegated approvals promptly

  Background:
    Given the user "delegateUser" is logged into the FPM application with delegation permissions
    And the WebSocket connection is active

  Scenario: Receive and interact with in-app notification upon approval delegation
    When another user "managerUser" delegates an approval request to "delegateUser"
    Then "delegateUser" should receive an in-app notification immediately
    And the notification should clearly indicate the delegation event and relevant details
    And the notification should be actionable
    When "delegateUser" interacts with the notification
    Then the approval details related to the delegation should be displayed

