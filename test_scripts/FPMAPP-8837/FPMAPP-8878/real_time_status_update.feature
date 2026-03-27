# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8878
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:38:40
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval request status updates
  As a requester
  I want to see real-time status updates of my approval requests
  So that I can track the progress without manual refresh

  Background:
    Given the user is logged in as a requester
    And an approval request with ID "REQ-12345" has been submitted and is in progress
    And a WebSocket connection for real-time updates is established

  Scenario: Receive real-time status update when approval stage changes
    When the backend triggers a status change event for approval request "REQ-12345" to "Approved"
    Then the requester should receive the status update immediately via WebSocket
    And the UI should reflect the new approval status "Approved" without manual refresh
    And no errors or delays should be observed in the update delivery
