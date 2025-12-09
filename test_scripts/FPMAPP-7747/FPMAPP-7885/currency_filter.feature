# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7885
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:16:21
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter in Transaction History

  Scenario: Filter transactions by currency
    Given I am on the transaction history page
    When I select "INR to JPY" from the currency filter
    Then I should see only "INR to JPY" conversions displayed
    And I should not see any "INR to USD" conversions
    And I should not see any "USD to JPY" conversions
