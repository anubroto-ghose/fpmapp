# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7229
# Epic: FPMAPP-7183
# Generated on: 2025-10-30 17:58:43
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter in Transaction History

  Scenario: Filter transactions by currency
    Given I have multiple currency conversions in history
      | currencyPair  |
      | INR to USD   |
      | INR to JPY   |
      | INR to EUR   |
    When I navigate to the transaction history page
    And I select "INR to JPY" from the currency filter
    And I apply the filter
    Then I should see only transactions for "INR to JPY"
    And I should not see transactions for other currency pairs
