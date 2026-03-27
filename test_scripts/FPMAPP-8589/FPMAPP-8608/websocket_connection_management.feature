# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8608
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:08:39
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: WebSocket Connection Management Under Resource Constraints
  As a user of FPM_UI
  I want immediate feedback on approval statuses and currency updates via real-time notifications
  So that I can rely on the system's stability and performance under load

  Background:
    Given the user "testuser" is logged into the FPM_UI
    And the WebSocket connection is established
    And system resource usage monitoring is active

  @medium
  Scenario: WebSocket connection remains stable during rapid backend events
    When the backend triggers 50 rapid real-time update events
    Then the WebSocket connection should remain stable without disconnects or errors
    And the UI should display all 50 real-time updates without lag or failure
    And resource usage should remain within acceptable limits
    And no excessive WebSocket reconnections or memory leaks should occur

  
  # Step Definitions (for reference, to be implemented in Java)
  # Given the user "testuser" is logged into the FPM_UI
  # Given the WebSocket connection is established
  # Given system resource usage monitoring is active
  # When the backend triggers 50 rapid real-time update events
  # Then the WebSocket connection should remain stable without disconnects or errors
  # Then the UI should display all 50 real-time updates without lag or failure
  # Then resource usage should remain within acceptable limits
  # Then no excessive WebSocket reconnections or memory leaks should occur
