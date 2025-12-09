# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7884
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:17:13
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Pagination for INR to JPY Transactions

  Scenario: User navigates through multiple pages of INR to JPY transactions
    Given the user has more than one page of INR to JPY transactions
    When the user navigates to the transaction history page
    And the user scrolls to the bottom or uses pagination controls
    And the user switches pages to view more INR to JPY entries
    Then the user can navigate between pages without data loss
    And each page consistently loads INR to JPY transactions