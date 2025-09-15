# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6208
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:49:37
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Pagination of INR to JPY currency conversion transaction history
  
  As a user with role-based approval permissions
  I want to navigate through multiple pages of INR to JPY transaction history
  So that I can see all transaction entries along with their audit trails and approval statuses

  Background:
    Given the user "testuser" has role "APPROVER_INR_JPY"
    And there are more than one page of INR to JPY transactions available with audit trails
    And role-based approval settings are enabled

  Scenario: Navigate through paginated INR to JPY transactions
    When the user navigates to the transaction history page filtered by from currency "INR" and to currency "JPY"
    Then the transaction table should load with the first page of INR to JPY transactions

    When the user views the audit trail for each transaction on the page
    Then the audit trail details should be displayed correctly

    When the user clicks the "Next" pagination control to move to the second page
    Then the transaction table should update to show the next page of INR to JPY transactions

    When the user verifies role-based permission filters on the transactions
    Then only transactions matching the user's role-based permissions should be displayed

    When the user navigates through all available pages
    Then the user should see consistent, accurate data without loss or duplication
    And the pagination performance should be acceptable

  Scenario Outline: Validate approval status and audit trail presence for transactions
    Given the user is on the transaction history page filtered by from currency "INR" and to currency "JPY"
    When the user inspects the transaction with Transaction ID <transactionId>
    Then the transaction should have approval status "<approvalStatus>"
    And the audit trail link should be present and open a modal with audit details

    Examples:
      | transactionId | approvalStatus |
      | 1000          | APPROVED       |
      | 1001          | PENDING        |
      | 1003          | APPROVED       |

  Scenario: Backward compatibility check for pagination behavior
    Given the user accesses transaction history page for INR to JPY without role-based filters
    When the user paginates through the transactions
    Then the pagination behavior should be consistent with legacy behavior
    And no regression in performance or data loading should occur