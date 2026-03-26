# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8791
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:30:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update without full page refresh
  As a user of the FPMApplication
  I want immediate feedback on approval and currency actions without full page refresh
  So that I can see the latest approval status and audit trail updates instantly

  Background:
    Given the user "testuser" is logged into the FPMApplication UI
    And the approval workflow is active
    And the user has pending approval tasks
    And a WebSocket connection is established and active

  Scenario: Approval status updates immediately on UI after external approval action
    When an approval action is triggered on a pending item by another user or system
    Then the approval status updates immediately in the UI reflecting the new state
    And no full page reload occurs
    And the UI component "ApprovalAuditTrailView" shows the updated audit trail entry in real-time
    And no error messages or UI glitches are observed
