# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8809
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:55:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update in FPM Tools UI
  
  As a user with approval permissions
  I want to see immediate, real-time feedback on approval actions and status changes
  So that I can be confident the UI reflects the current backend state without manual refresh

  Background:
    Given the user "approverUser" is logged into the FPM application with approval permissions
    And the WebSocket connection is established and active
    And there is a pending approval request with ID "2001" from another user

  Scenario: Approval status updates immediately after approval action by another user
    When another user approves the pending request with ID "2001"
    Then the approval status indicator for request "2001" updates immediately to "Approved"
    And the page does not refresh
    And the UI remains responsive and consistent with the backend state

  Scenario: Approval status updates immediately after rejection action by another user
    When another user rejects the pending request with ID "2001"
    Then the approval status indicator for request "2001" updates immediately to "Rejected"
    And the page does not refresh
    And the UI remains responsive and consistent with the backend state
