# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8616
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:02:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time in-app notification update on approval status change
  
  As an approver or requester
  I want to receive automatic in-app notifications
  So that I can see approval status changes immediately without refreshing

  Background:
    Given the user "approverUser" is logged in to the FPM_UI
    And the WebSocket notification service is active and connected
    And an approval request with ID "REQ-12345" exists with status "PENDING"

  Scenario: Approval request status changes to APPROVED and notification updates in real-time
    When the approval request "REQ-12345" status is changed to "APPROVED" via the workflow engine
    Then the in-app notification area updates immediately
    And the notification message contains "Approval request REQ-12345 has been APPROVED."
    And the notification timestamp is recent
    And the notification delivery is logged for audit purposes
    And no delay or failure in notification delivery is observed

  Scenario: Approval request status changes to REJECTED and notification updates in real-time
    When the approval request "REQ-12345" status is changed to "REJECTED" via the workflow engine
    Then the in-app notification area updates immediately
    And the notification message contains "Approval request REQ-12345 has been REJECTED."
    And the notification timestamp is recent
    And the notification delivery is logged for audit purposes
    And no delay or failure in notification delivery is observed
