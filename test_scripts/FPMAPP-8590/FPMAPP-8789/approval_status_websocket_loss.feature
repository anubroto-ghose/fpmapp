# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8789
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:00:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status Real-Time Update with WebSocket Connection Loss
  As a logged-in user of the FPMApplication
  I want to verify that when the WebSocket connection is lost,
  the approval status does not update in real-time and no full page reload occurs,
  and the UI shows a connection error indicator without error messages.

  Background:
    Given the user is logged into the FPMApplication UI
    And the user is on the approvals page

  Scenario: Approval status does not update when WebSocket connection is lost
    Given the WebSocket connection is lost
    When an approval status change is triggered by another user or system
    Then the approval status should not update in real-time
    And no full page reload should occur
    And the UI should show a connection error indicator
    And no error messages related to approval update failures should be shown
