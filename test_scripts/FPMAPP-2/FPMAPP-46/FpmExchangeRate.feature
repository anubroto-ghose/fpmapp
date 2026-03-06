# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-46
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:31:05
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: View Current and Historical Exchange Rates

  Scenario: Validate viewing of current and historical exchange rates
    Given the system has historical exchange rates stored in the database
    When I navigate to the exchange rates page
    And I select the currency pair "USD/EUR"
    Then the current rate should be displayed correctly
    And the historical rates should be accessible and accurate