# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8638
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:47:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: WebSocket connection interruption and graceful reconnection with state resynchronization
  
  As an end user
  I want immediate feedback on approval statuses and currency rate updates via real-time UI updates
  So that I can trust the application to show accurate and up-to-date information even after network interruptions

  Background:
    Given the user "testuser" is logged into the FPM Tools application
    And WebSocket connections to "/fpmcamunda/approvals/updates" and "/fpm/currency/rates/updates" are established

  Scenario: WebSocket disconnection and automatic reconnection with state resynchronization
    When a network interruption causes the WebSocket connections to disconnect
    Then the UI should detect the WebSocket disconnection promptly
    And the UI should automatically attempt to reconnect without user intervention
    When the WebSocket connections are re-established
    Then the UI should resynchronize the approval statuses with the backend
    And the UI should resynchronize the currency rates with the backend
    And no stale or inconsistent data should be displayed during or after reconnection
    When new notifications for approvals, delegations, and overrides are sent after reconnection
    Then the notifications should appear immediately without delay

