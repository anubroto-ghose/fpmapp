# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4869
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:12:35
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: WebSocket Connection for Real-Time Notifications

  Scenario: Successful WebSocket connection
    Given the user is logged into the application
    When the application is opened in a web browser
    Then the console should show WebSocket connection attempts
    And the WebSocket connection should be successfully established without errors
