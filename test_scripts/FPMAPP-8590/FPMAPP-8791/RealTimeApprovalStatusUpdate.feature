# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8791
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:14:36
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update without full page refresh
  As a user of the FPMApplication UI
  I want to see immediate feedback on approval actions without manually refreshing the page
  So that I can have up-to-date information and audit trail visibility

  Background:
    Given the user "testuser" is logged into the FPMApplication UI
    And the approval workflow is active with pending approval tasks for the user
    And a WebSocket connection is established and active

  Scenario: Approval status updates immediately on UI after external approval action
    When an approval action is triggered on a pending item by another user or system
    Then the approval status on the UI updates immediately to "Approved"
    And the page does not perform a full reload
    And the ApprovalAuditTrailView shows the updated audit trail entry with action "Approved" and user "approverUser"
    And no error messages or UI glitches are observed
