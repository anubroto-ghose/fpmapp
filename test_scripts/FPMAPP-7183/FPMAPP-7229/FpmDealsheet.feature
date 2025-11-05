# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7229
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:05:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter Functionality

  Scenario: User filters currency conversions
    Given the user is on the transaction history page
    When the user selects "INR to JPY" from the currency filter
    And clicks the filter button
    Then only "INR to JPY" conversions should be displayed
    And other currency conversions should be excluded from view
