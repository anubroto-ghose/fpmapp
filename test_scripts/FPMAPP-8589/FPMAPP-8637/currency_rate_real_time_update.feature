# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8637
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:48:03
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Rate Update
  As an end user
  I want immediate feedback on currency rate updates via real-time UI updates
  So that I can see accurate and up-to-date currency rates without refreshing the page

  Background:
    Given the user is logged into the FPM Tools application
    And a WebSocket connection to "/fpm/currency/rates/updates" is established

  Scenario: Currency rate update is pushed and displayed instantly
    When the backend triggers a currency rate change from USD to EUR with rate 0.90 and override true
    Then the UI receives the currency rate update event in real-time
    And the currency rate from USD to EUR updates instantly to "0.90" on the UI
    And the override information is displayed correctly
    And no delay or lag is observed in the update display

  Scenario Outline: Multiple currency rate updates are reflected instantly
    When the backend triggers a currency rate change from <fromCurrency> to <toCurrency> with rate <rate> and override <override>
    Then the UI receives the currency rate update event in real-time
    And the currency rate from <fromCurrency> to <toCurrency> updates instantly to "<rate>" on the UI
    And the override information is displayed correctly if <override> is true
    And no delay or lag is observed in the update display

    Examples:
      | fromCurrency | toCurrency | rate | override |
      | USD          | EUR        | 0.90 | true     |
      | GBP          | USD        | 1.35 | false    |
      | EUR          | JPY        | 130  | true     |
