# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8607
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:09:17
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Rate Update Notifications
  As a user of FPM_UI
  I want to receive immediate feedback on currency rate updates
  So that I can see the latest rates without refreshing the page

  Background:
    Given the user "testuser" is logged into the FPM_UI
    And the WebSocket connection is active and receiving data

  Scenario: Currency rates update instantly upon backend WebSocket event
    When the backend pushes a currency rate update for "EUR" with rate "0.90" via WebSocket
    Then the currency rate display for "EUR" updates instantly to "0.90" without page refresh
    And a notification for the currency change appears with message containing "EUR rate updated to 0.90"
    And no UI glitches or delays occur during the update
