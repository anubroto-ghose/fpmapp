# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8790
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:29:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Approval Status Update
  As a user of the FPM Application
  I want the approval status to update immediately and correctly without UI flicker
  So that I can trust the displayed approval information is accurate and up-to-date

  Background:
    Given the user is logged into the FPMApplication UI
    And the WebSocket connection is established and active

  Scenario: Rapid consecutive approval status changes update UI correctly without flicker or errors
    When multiple approval status changes are triggered rapidly on an approval item
    Then the approval status updates immediately and correctly for each change
    And the ApprovalAuditTrailView component reflects all changes accurately
    And no UI flicker or visual glitches occur
    And no error messages are shown
