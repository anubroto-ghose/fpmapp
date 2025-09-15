# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6239
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:26:21
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Transaction History Pagination
  
  Background:
    Given user "testuser" with role "APPROVER" is authenticated
    And role-based approval and audit trail features are activated
    And there are multiple pages of INR to JPY transactions

  Scenario: Navigate through multiple pages of INR to JPY transaction history
    When user navigates to the transaction history page for INR to JPY
    Then the first page should display 10 accurate transactions
    When user navigates to page 2
    Then the second page should display 10 accurate transactions
    When user navigates to page 3
    Then the third page should display 5 accurate transactions
    And audit trail should record pagination navigation actions
    And role-based approval status is visible and active

  Scenario Outline: Verify transactions on each page are INR to JPY
    Given user is on transaction history page
    When user navigates to page <pageNumber>
    Then transactions should only contain records with from currency "INR" and to currency "JPY"
    And the transactions count on page should be <expectedCount>

    Examples:
      | pageNumber | expectedCount |
      | 1          | 10            |
      | 2          | 10            |
      | 3          | 5             |
