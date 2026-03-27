# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8636
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:48:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status update
  As an end user
  I want immediate feedback on approval statuses and currency rate updates via real-time UI updates
  So that I can see approval changes without refreshing the page

  Background:
    Given the user "testuser" is logged into the FPM Tools application
    And a WebSocket connection to "/fpmcamunda/approvals/updates" is established

  Scenario: Approval status update is received and displayed correctly in real-time
    When the backend triggers an approval status change for approval ID "APPROVAL123" with status "APPROVED"
    Then the UI receives the approval status update event in real-time
    And the approval status displayed on the UI updates immediately without page refresh
    And a notification for the approval status change appears promptly and accurately
    And the displayed approval ID and status match the backend state
