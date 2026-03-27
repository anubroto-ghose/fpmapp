# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8893
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:28:40
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status updates on FPM Tools UI
  
  As a requester or approver
  I want to receive real-time updates and notifications on approval status changes
  So that I can see the latest approval status without manual page refresh

  Background:
    Given the user is logged in as a "requester"
    And the WebSocket connection is established between backend and UI
    And there are pending approval requests in the system

  Scenario: Approval status changes from Pending to Approved and UI updates in real-time
    When the user triggers an approval status change to "Approved" on financial request with ID 1001
    Then the UI receives the status update via WebSocket
    And the approval status indicator for request ID 1001 updates to "Approved" immediately
    And no manual page refresh is required to see the change
    And no errors or delays occur in update delivery

  Scenario: Approval status changes from Pending to Rejected and UI updates in real-time
    When the user triggers an approval status change to "Rejected" on financial request with ID 1002
    Then the UI receives the status update via WebSocket
    And the approval status indicator for request ID 1002 updates to "Rejected" immediately
    And no manual page refresh is required to see the change
    And no errors or delays occur in update delivery
