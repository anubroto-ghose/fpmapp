# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4871
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:06:29
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Fallback Notification Mechanism

  Scenario: User receives fallback notification on WebSocket failure
    Given the user is logged into the application
    And the WebSocket connection is intentionally disconnected
    When the user triggers an approval status change
    Then the application should display a fallback notification
    And the fallback notification should indicate the approval status change