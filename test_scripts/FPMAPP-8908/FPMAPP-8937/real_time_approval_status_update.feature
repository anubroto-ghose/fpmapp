# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8937
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:52:44
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Approval Status Updates
  As a user with approval permissions
  I want to see real-time status updates on my approval requests and uploads
  So that I do not need to refresh the page to get the latest status

  Background:
    Given the user is logged in with role "Approver"
    And there are approval requests with statuses "Pending" and "In Progress"
    And a WebSocket connection is established and active

  Scenario: Approval status updates dynamically when approved by another user
    When another user approves the approval request with ID "12345"
    Then the approval status for request "12345" should update to "Approved" in real-time
    And the delegation status should be displayed as "Delegated"
    And the role-based approval indicator should show "Approver"
    And the page should not refresh

  Scenario: Approval status updates dynamically when rejected by another user
    When another user rejects the approval request with ID "67890"
    Then the approval status for request "67890" should update to "Rejected" in real-time
    And the delegation status should be displayed as "None"
    And the role-based approval indicator should show "Delegate"
    And the page should not refresh
