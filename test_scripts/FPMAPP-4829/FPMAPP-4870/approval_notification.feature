# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4870
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:07:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Notification Reception

  Scenario: User receives real-time notification about approval status change
    Given the user is logged into the application
    And the user is connected to the WebSocket
    When an approval status change is triggered from the backend
    Then the user should receive a notification about the approval status change in real-time without refreshing the page
