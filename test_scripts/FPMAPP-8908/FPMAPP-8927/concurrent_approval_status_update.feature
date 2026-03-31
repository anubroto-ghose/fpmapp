# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8927
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:02:40
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Concurrent Approval Status Updates
  As a requester
  I want to receive real-time status updates and notifications for my approval requests
  So that I can see consistent and accurate approval statuses without conflicts

  Background:
    Given multiple users have access to update the same approval request
    And the requester is logged into the FPMApplication UI with real-time updates enabled

  Scenario: Simultaneous status updates from two users
    When two users simultaneously update the status of the same approval request
      | User  | Status   |
      | user1 | Approved |
      | user2 | Rejected |
    Then the system resolves concurrent updates gracefully without data loss
    And the requester sees the final consistent status in real time
    And notifications reflect the correct final status
    And no duplicate or conflicting notifications are sent
    And the UI does not show flickering or inconsistent states
