# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4869
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:08:54
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: WebSocket Connection

  Scenario: Successful WebSocket connection for real-time notifications
    Given the user is logged into the application
    When the application is opened in a web browser
    Then the WebSocket connection should be established without errors
    And the WebSocket status should indicate "Connected"