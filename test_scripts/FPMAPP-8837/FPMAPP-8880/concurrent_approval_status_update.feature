# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8880
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:37:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Concurrent Approval Status Updates
  As a requester
  I want to see real-time status updates of my approval requests
  So that I can track approval progress without conflicts or inconsistencies

  Background:
    Given the user is logged in as a requester
    And the real-time communication channel is active

  Scenario: System handles multiple concurrent approval status updates without conflicts
    Given multiple concurrent approval status updates are triggered on the backend
    When the user views the approval request status
    Then the UI should reflect all status updates in the correct order
    And no conflicting or overlapping status information should be displayed
    And the system should maintain transactional integrity and concurrency control
    And the user experience should remain smooth without performance degradation
