# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8827
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:44:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History Pagination for INR to JPY entries
  
  As a financial user with role-based approval rights
  I want to navigate through multiple pages of INR to JPY transactions
  So that I can verify audit trails and approval statuses without data loss

  Background:
    Given the user is logged in with role "FINANCE_APPROVER"
    And the transaction history contains more than one page of INR to JPY transactions

  Scenario: Navigate through transaction history pages and verify data consistency
    When the user navigates to the transaction history page
    Then the first page of INR to JPY transactions is displayed with audit trail and approval status

    When the user navigates to page 2
    Then the second page of INR to JPY transactions is displayed with audit trail and approval status

    When the user navigates to page 3
    Then the third page of INR to JPY transactions is displayed with audit trail and approval status

  Scenario: Verify pagination performance and data integrity
    Given the user is on the transaction history page
    When the user switches between pages 1 and 3
    Then each page loads within acceptable performance limits
    And no data loss occurs
    And audit trail and approval status indicators are correctly displayed on all pages
