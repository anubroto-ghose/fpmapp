# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7208
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:13:54
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction Pagination

  Scenario: User navigates through paginated INR to JPY transactions
    Given the user is on the transaction history page
    And the user has more than one page of INR to JPY transactions
    When the user views the first page of transactions
    Then the user should see a list of INR to JPY transactions

    When the user navigates to the next page
    Then the next set of INR to JPY transactions should be displayed
    And the user can return to the previous page
    And all transactions should load without any data loss
