# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7228
# Epic: FPMAPP-7183
# Generated on: 2025-10-30 17:59:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Pagination for INR to JPY Transactions

  Scenario: User navigates through multiple pages of INR to JPY transactions
    Given the user has more than one page of INR to JPY transactions
    When the user navigates to the transaction history page
    And the user clicks on the next page button
    Then the user should see the next set of INR to JPY transactions
    And the transactions should not be empty
    And all transactions should be of currency type "INR to JPY"