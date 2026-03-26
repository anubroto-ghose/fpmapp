# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8788
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:28:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update without full page refresh
  As a user of the FPMApplication
  I want immediate feedback on approval and currency actions without full page reload
  So that I can see the latest approval status and audit trail updates instantly

  Background:
    Given the user "testuser" is logged into the FPMApplication UI
    And the WebSocket connection is established and active

  Scenario: Approval status updates immediately when changed by another user or system
    Given an approval item with ID "12345" is displayed on the approvals page
    When an approval status change to "Approved" is triggered externally for approval ID "12345"
    Then the approval status for ID "12345" updates immediately in the UI without a full page reload
    And the ApprovalAuditTrailView component reflects the new approval status
    And no error messages are shown
    And the UI does not flicker or delay beyond expected real-time latency
