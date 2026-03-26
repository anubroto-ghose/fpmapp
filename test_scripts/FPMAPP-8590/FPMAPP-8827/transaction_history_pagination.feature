# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8827
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:56:36
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History Pagination with Role-Based Approval and Audit Trail
  
  As a user with role-based approval permissions
  I want to navigate through multiple pages of INR to JPY transactions
  So that I can verify audit trail and approval indicators are correctly displayed on each page

  Background:
    Given the user is logged in with role "Manager"
    And the system has more than one page of INR to JPY transactions with role-based approval and audit trail data

  Scenario: User navigates through transaction history pages and verifies data consistency
    When the user navigates to the transaction history page
    Then the first page of INR to JPY transactions is displayed with correct approval status and audit trail indicators

    When the user scrolls to the bottom of the page
    And the user clicks the "Next" pagination button
    Then the second page of INR to JPY transactions is displayed with correct approval status and audit trail indicators

    When the user clicks the "Previous" pagination button
    Then the first page of INR to JPY transactions is displayed again with correct approval status and audit trail indicators

  Scenario Outline: Verify audit trail and approval indicators on each page
    Given the user is on page <pageNumber> of the transaction history
    Then all transactions on the page have currency pair "INR/JPY"
    And each transaction displays a valid approval status
    And each transaction has an audit trail link

    Examples:
      | pageNumber |
      | 1          |
      | 2          |
