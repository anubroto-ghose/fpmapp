# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7229
# Epic: FPMAPP-7183
# Generated on: 2025-10-30 17:58:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter in Transaction History

  Scenario: Filter transactions by currency
    Given I have multiple currency conversions in history
      | currencyPair  |
      | INR to JPY   |
      | INR to USD   |
      | INR to EUR   |
    When I navigate to the transaction history page
    And I select "INR to JPY" from the currency filter
    Then I should see only "INR to JPY" conversions displayed
    And I should not see "INR to USD" conversions
    And I should not see "INR to EUR" conversions