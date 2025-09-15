# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6220
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:39:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Real-Time UI Update
  As a finance administrator
  I want to see currency rate changes and overrides reflected in the UI in real-time
  So that I do not have to reload the page to see the updates

  Background:
    Given the currency real-time update WebSocket service is running
    And the currency panel UI page is loaded and connected to the WebSocket feed

  Scenario: UI updates currency rates and override flags in real-time without page reload
    When the administrator triggers a currency override for "USD" with new rate 1.25 and reason "Test override for automation"
    Then the UI currency display panel should update the "USD" currency rate to 1.25 in real-time
    And the override flag for "USD" should show as "OVERRIDDEN"
    And the page should not reload or require manual refresh during this update
    And no UI error messages should be visible
