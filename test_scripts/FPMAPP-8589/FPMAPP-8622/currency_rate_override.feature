# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8622
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:59:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Override Immediate UI Update
  As an admin user with permission to override currency rates
  I want the currency exchange rate UI to update immediately after an override
  So that I receive real-time feedback without refreshing the page

  Background:
    Given the admin user is logged in
    And the WebSocket connection to "/fpm/ui/notifications/ws" is active
    And the currency exchange rate UI elements are visible

  @Positive
  Scenario: Successful currency rate override updates UI immediately
    When the admin overrides the currency rate from "USD" to "EUR" with rate "0.85"
    Then the currency exchange rate display updates to "0.85" immediately
    And a notification alert is shown confirming the override
    And the updated rate is consistent with the override input

  @Negative
  Scenario Outline: Client-side validation prevents invalid override inputs
    When the admin attempts to override the currency rate with invalid rate "<invalidRate>"
    Then a validation error message "<errorMessage>" is displayed
    And the override is not submitted

    Examples:
      | invalidRate | errorMessage          |
      | -0.5        | Invalid rate          |
      | abc         | Invalid rate          |
      |            | Rate is required      |

  Scenario: Currency rate display updates without page refresh on server push
    When the server pushes a currency rate update from "USD" to "EUR" with rate "0.90"
    Then the currency exchange rate display updates to "0.90" immediately
