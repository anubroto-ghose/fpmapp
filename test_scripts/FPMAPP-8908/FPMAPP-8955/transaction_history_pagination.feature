# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8955
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:36:32
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History Pagination
  As a financial user
  I want to navigate through multiple pages of INR to JPY transactions
  So that I can view all transactions without data loss or UI issues

  Background:
    Given the user is logged in with role "FinancialUser"
    And the user has more than one page of INR to JPY transactions
    And role-based approval and real-time currency integration features are active

  Scenario: Navigate through transaction history pages
    When the user navigates to the transaction history page
    Then the first page of INR to JPY transactions is displayed

    When the user navigates to the next page
    Then the next page of INR to JPY transactions is displayed

    When the user navigates to the last page
    Then the last page of INR to JPY transactions is displayed

    And no data loss or UI glitches occur
    And approval statuses and currency data remain consistent across pages

  Scenario Outline: Verify pagination controls and data consistency
    Given the user is on the transaction history page
    When the user navigates to page <pageNumber>
    Then the page <pageNumber> of INR to JPY transactions is displayed
    And all transactions on page <pageNumber> have correct approval status and currency conversion

    Examples:
      | pageNumber |
      | 1          |
      | 2          |
      | 3          |
