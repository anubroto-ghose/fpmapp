# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8817
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:49:37
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rates Display
  As a financial analyst
  I want to see real-time and historical currency exchange rates integrated into the system
  So that I can make informed financial decisions based on up-to-date currency data

  Background:
    Given the user is logged into the system with access to the currency exchange rate UI
    And real-time currency exchange rates are being fetched and stored correctly

  Scenario: UI displays latest real-time currency exchange rates with automatic updates
    When the user navigates to the currency exchange rates display page
    Then the UI displays the current currency exchange rates with accurate timestamps
    And the override flags and metadata are correctly shown
    When the user waits for the configured interval for real-time updates
    Then the UI automatically refreshes and displays updated currency exchange rates without manual page reload
    And no stale or outdated data is shown during normal operation

  Scenario Outline: Override flags and timestamps are correctly indicated in the UI
    Given the currency pair <currencyPair> has an exchange rate of <rate> with timestamp <timestamp> and override flag <overrideFlag>
    When the user views the currency exchange rates display page
    Then the UI shows the currency pair <currencyPair> with rate <rate>
    And the timestamp <timestamp> is displayed
    And the override flag is <overrideFlag>

    Examples:
      | currencyPair | rate  | timestamp           | overrideFlag |
      | USD/EUR      | 0.85  | 2026-03-26T15:00:00Z | true        |
      | USD/JPY      | 110.25| 2026-03-26T15:00:00Z | false       |
