# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6191
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:02:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status updates via WebSocket
  
  As a requester
  I want to see a full audit trail and real-time status tracking for my approval requests
  So that I am immediately informed of any changes without refreshing the page

  Background:
    Given the requester "requesterUser" is logged in
    And the requester is viewing the approval requests page
    And the WebSocket connection to "/ws/approvals" is established
    And there is an approval request with id "approval-12345" with status "PENDING"

  Scenario: Approval status updates instantly via WebSocket push
    When a separate user "approverUser" approves the request with id "approval-12345"
    Then the approval status for "approval-12345" updates to "APPROVED" in the UI without page refresh
    And the role-based status indicators update appropriately
    And no UI errors are observed during the update

  Scenario Outline: Approval status updates for various roles and granular status indicators
    Given there is an approval request with id "<approvalId>" and initial status "<initialStatus>"
    When a user "<user>" with role "<role>" changes the approval status to "<newStatus>"
    Then the UI displays the updated status "<newStatus>" with correct role-based indicators

    Examples:
      | approvalId      | initialStatus | user          | role          | newStatus  |
      | approval-12345  | PENDING       | approverUser  | Manager       | APPROVED  |
      | approval-23456  | PENDING       | delegateUser  | Delegate      | DELEGATED |
      | approval-34567  | DELEGATED     | approverUser  | SeniorManager | APPROVED  |

  Scenario: No manual intervention needed to see status update
    When the status of approval request "approval-12345" changes to "REJECTED" by "approverUser"
    Then the UI automatically updates the status to "REJECTED" without page refresh or reload

  Scenario: UI performs well and no degradation during multiple rapid status updates
    Given multiple status updates occur rapidly on approval request "approval-12345"
    When the updates are pushed via WebSocket
    Then the UI reflects all status changes correctly
    And no performance degradation or errors are present
