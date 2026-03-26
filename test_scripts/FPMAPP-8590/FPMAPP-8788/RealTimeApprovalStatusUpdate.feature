# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8788
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:00:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update without full page refresh
  As a user of the FPMApplication UI
  I want immediate feedback on approval and currency actions without full page refresh
  So that I can see approval status changes instantly and audit trail updates

  Background:
    Given the user is logged into the FPMApplication UI
    And a WebSocket connection is established and active

  Scenario: Approval status updates immediately when changed by another user or system
    Given an approval item with request ID "REQ-12345" is displayed on the UI
    When an approval status change is triggered by another user session or backend for request ID "REQ-12345"
    Then the approval status for request ID "REQ-12345" updates immediately in the UI without a full page reload
    And the update is reflected in the ApprovalAuditTrailView component
    And no UI flicker or delay beyond expected real-time latency occurs
    And no error messages are shown
