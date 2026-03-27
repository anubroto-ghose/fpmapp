# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8621
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:59:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time UI update on approval status change
  As an end user
  I want immediate feedback on approval statuses via real-time UI updates
  So that I can see approval changes without refreshing the page

  Background:
    Given a user "testuser" with role "Approver" is logged in
    And the approval workflow UI is loaded
    And a WebSocket connection to "/fpm/ui/notifications/ws" is established

  Scenario: Approval status changes to APPROVED and UI updates immediately
    When the backend triggers an approval status change to "APPROVED"
    Then the UI approval status indicator updates to "APPROVED" immediately
    And the status indicator CSS class contains "approved"
    And no errors are shown on the UI

  Scenario: Approval status changes to REJECTED and UI updates immediately
    When the backend triggers an approval status change to "REJECTED"
    Then the UI approval status indicator updates to "REJECTED" immediately
    And the status indicator CSS class contains "rejected"
    And no errors are shown on the UI

  Scenario: Approval status changes to PENDING and UI updates immediately
    When the backend triggers an approval status change to "PENDING"
    Then the UI approval status indicator updates to "PENDING" immediately
    And the status indicator CSS class contains "pending"
    And no errors are shown on the UI
