# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8939
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:51:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: WebSocket Connection Interruption Handling
  As a user of the FPM Tools application
  I want to see real-time status updates on my approval requests and uploads without refreshing the page
  So that I am informed about connection issues and data is not lost

  Background:
    Given the user is logged into the application
    And the WebSocket connection is established and active
    And approval requests or uploads are in progress

  Scenario: Simulate WebSocket connection interruption and recovery
    When the WebSocket connection is interrupted
    Then the UI should show a notification about the connection loss
    And no stale or outdated data should be displayed
    When the WebSocket connection is restored
    Then the UI should resume real-time updates
    And the current status should be reflected accurately

  Scenario Outline: Verify UI behavior during WebSocket interruptions
    Given the user is logged in
    And the WebSocket connection is active
    When the WebSocket connection is <connection_state>
    Then the UI should <expected_ui_behavior>

    Examples:
      | connection_state | expected_ui_behavior                         |
      | interrupted      | show a connection loss notification        |
      | restored        | resume real-time updates and update status |
