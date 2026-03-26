# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8809
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:43:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update in UI
  As a user with approval permissions
  I want to see immediate, real-time feedback on approval actions and status changes
  So that I do not need to refresh the page to see updated approval statuses

  Background:
    Given the user "approverUser" is logged into the FPMApplication with approval permissions
    And a WebSocket connection is established and active
    And there is a pending approval request with ID "12345"

  Scenario: Approval status updates immediately on UI after another user approves
    When another user "managerUser" approves the pending request with ID "12345"
    Then the approval status indicator for request "12345" updates immediately to "Approved"
    And the page does not refresh
    And the UI remains responsive and consistent with the backend state

  Scenario: Approval status updates immediately on UI after another user rejects
    When another user "managerUser" rejects the pending request with ID "12345"
    Then the approval status indicator for request "12345" updates immediately to "Rejected"
    And the page does not refresh
    And the UI remains responsive and consistent with the backend state
