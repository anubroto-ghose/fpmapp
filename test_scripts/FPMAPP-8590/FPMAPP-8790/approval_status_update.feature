# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8790
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:08:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Approval Status Update
  As a financial user
  I want the approval status to update immediately and correctly without UI flicker
  So that I can trust the system reflects the latest approval state accurately

  Background:
    Given the user "testuser" is logged into the FPM Tools UI
    And the WebSocket connection for approval status updates is active

  Scenario: Rapid consecutive approval status changes update UI correctly without flicker
    When multiple approval status changes are triggered rapidly from different sessions
    Then the approval status component updates immediately and correctly for each change
    And the ApprovalAuditTrailView component reflects all changes accurately
    And no UI flicker or visual glitches occur
    And no error messages are shown on the UI
