# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8606
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:09:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time update of approval status via WebSocket
  As a user of FPM_UI with approval permissions
  I want immediate feedback on approval statuses via real-time notifications
  So that I can see approval status changes without refreshing the page

  Background:
    Given the user "approverUser" is logged into FPM_UI with role "ROLE_APPROVER"
    And a WebSocket connection to the backend notification service is established and active

  Scenario: Approval status updates immediately upon backend change
    Given a pending approval request with ID "REQ12345" exists
    When the backend approves the request with ID "REQ12345"
    Then the UI approval status for request "REQ12345" updates to "APPROVED" immediately
    And the page does not refresh
    And the approval status update matches the backend state
    And the audit trail logs the approval status change
    And no errors or delays occur in the WebSocket communication
