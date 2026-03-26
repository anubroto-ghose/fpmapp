# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8789
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:13:17
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status UI Update on WebSocket Connection Loss
  As a logged-in user of the FPMApplication
  I want the approval status UI to not update in real-time if the WebSocket connection is lost
  So that the UI remains stable and does not reload or show errors unnecessarily

  Background:
    Given the user is logged into the FPMApplication UI
    And the user is on the approval list page

  Scenario: Approval status does not update when WebSocket connection is lost
    Given the WebSocket connection is lost
    When an approval status change is triggered by another user or system
    Then the approval status on the UI does not update in real-time
    And no full page reload occurs
    And a connection error indicator is displayed
    And no error messages related to approval update failures are shown
