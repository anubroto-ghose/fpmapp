# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8817
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:51:11
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rates Display
  As a financial analyst
  I want to see real-time and historical currency exchange rates integrated into the system
  So that I can make informed financial decisions based on up-to-date currency data

  Background:
    Given the user is logged into the FPM Tools system with access to the currency exchange rate UI
    And real-time currency exchange rates are being fetched and stored correctly

  Scenario: Display latest real-time currency exchange rates with automatic updates
    When the user navigates to the currency exchange rates display page
    Then the UI displays the latest currency exchange rates with accurate timestamps
    And the override flags and metadata are visible and correctly represented
    When the configured interval for real-time updates passes
    Then the UI automatically refreshes and displays updated currency exchange rates without manual page reload
    And no stale or outdated data is shown during normal operation
