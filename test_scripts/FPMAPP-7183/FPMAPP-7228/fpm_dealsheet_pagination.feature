# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7228
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:06:20
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Pagination for INR to JPY Transactions

  Scenario: User navigates through multiple pages of INR to JPY entries
    Given the user has multiple pages of INR to JPY transactions
    When the user navigates to the transaction history page
    And the user scrolls to the bottom of the page
    And the user switches to the next page
    Then the user should see INR to JPY transactions without data loss
    And each page should consistently load INR to JPY transactions.