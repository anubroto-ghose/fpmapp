# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8791
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:02:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update without full page refresh
  As a user of the FPMApplication
  I want immediate feedback on approval and currency actions without full page refresh
  So that I can see approval status updates and audit trail changes in real-time

  Background:
    Given the user "user1" is logged into the FPMApplication UI
    And the approval workflow is active
    And the user has pending approval tasks
    And a WebSocket connection is established and active

  Scenario: Approval status updates immediately on external approval action
    When an approval action is triggered on a pending item "REQ123" by another user "user2"
    Then the approval status for request "REQ123" updates immediately to "Approved" on the UI
    And the page does not perform a full reload
    And the ApprovalAuditTrailView shows the updated audit trail entry with comment "Approved by user2" and timestamp "2026-03-26T15:00:00"
    And no error messages or UI glitches are observed
