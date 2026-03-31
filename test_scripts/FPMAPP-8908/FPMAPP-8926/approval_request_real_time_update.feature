# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8926
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:03:39
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval request status updates
  As a requester
  I want to receive real-time status updates and notifications for my approval requests
  So that I can see the current status and history without refreshing the page

  Background:
    Given the user "testuser" is logged into the FPMApplication UI
    And the WebSocket connection is established and active
    And the user has approval requests visible in the UI

  Scenario: Approval request status updates in real-time
    When a status update is triggered on an approval request "AR-1001" from another user or system
    Then the UI updates immediately to reflect the new status "Approved"
    And the status history is updated and visible in real time
    And no stale or conflicting data is shown
    And the WebSocket connection remains stable during updates
