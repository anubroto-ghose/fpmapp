# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6222
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:38:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Approval Status Updates on Approval Details Page
  @FPMAPP-6172-TC02
  Scenario: Approval status updates immediately on UI without manual refresh
    Given a user logged in as a requester
    And approval requests exist in various states
    And a WebSocket connection to "/api/fpm/approvals/status" is active
    When the user opens the approval request details page for an approval with ID "12345"
    Then the approval status "PENDING" is displayed on the UI
    When an approval action "approve" is simulated on the backend for the approval ID "12345"
    Then the approval status updates to "APPROVED" immediately on the UI without manual refresh
    And the displayed approval details reflect the updated role-based current approval state
    And the WebSocket connection remains stable with no dropped messages

