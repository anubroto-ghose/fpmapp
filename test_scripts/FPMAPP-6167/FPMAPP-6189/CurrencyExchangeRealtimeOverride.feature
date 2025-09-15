# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6189
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:04:28
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time UI update of currency exchange rates with admin override indications
  
  As a finance administrator
  I want the currency exchange rates UI to update in real-time
  So that I can see overridden rates immediately without refreshing the page

  Background:
    Given the WebSocket client is connected to the currency updates WebSocket endpoint
    And the system has currency exchange rates with overrides present

  Scenario: Display initial currency exchange rates including overrides
    When the finance administrator opens the currency exchange rate dashboard
    Then the UI displays the current currency exchange rates
    And the UI shows override indicators on overridden rates
    And no stale data or UI glitches are present

  Scenario: Receive real-time currency rate override update
    Given the finance administrator is viewing the currency exchange rate dashboard
    When an admin performs an override on a currency rate "EUR" with new rate "0.85" and reason "Year end adjustment"
    Then the UI receives a real-time update through WebSocket
    And the overridden rate for "EUR" is refreshed on the page with the new value
    And an override indicator is shown next to the "EUR" currency rate
    And no page reload or manual refresh is required
    And the WebSocket connection remains stable
