# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8789
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:28:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status Real-time Update with WebSocket Connection Loss
  
  As a user of the FPMApplication UI
  I want to ensure that when the WebSocket connection is lost
  The approval status does not update in real-time and no full page reload occurs
  
  Background:
    Given the user "testuser" is logged into the FPMApplication UI
    And the WebSocket connection is active

  Scenario: Approval status does not update when WebSocket connection is lost
    Given the WebSocket connection is lost
    When an approval status change is triggered by another user or system
    Then the approval status on the UI does not update in real-time
    And no full page reload occurs
    And a connection error indicator is displayed
    And no error messages related to approval update failures are shown
