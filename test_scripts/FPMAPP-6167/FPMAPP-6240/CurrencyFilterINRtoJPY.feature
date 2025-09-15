# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6240
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:25:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency conversion filter for INR to JPY with role-based approval and audit compliance

  As a logged-in user with appropriate role-based permissions
  I want to filter transaction history to show only INR to JPY currency conversion transactions
  So that I can view only relevant approved or pending transactions with audit trail integrity

  Background:
    Given a user "roleBasedUser" with role "APPROVER_LEVEL_1" is logged in
    And the backend has currency conversion records:
      | From | To  | Amount   | Status   |
      | INR  | USD | 1000.00  | APPROVED |
      | INR  | JPY | 150000.0 | APPROVED |
      | INR  | JPY | 75000.0  | PENDING  |
      | INR  | USD | 500.00   | APPROVED |

  Scenario: Filter currency conversions to show only INR to JPY transactions
    When the user navigates to the transaction history page
    And the user applies currency filter from "INR" to "JPY"
    Then only transactions with "INR" as from currency and "JPY" as to currency are displayed
    And displayed transactions belong only to the logged in user's role based approvals
    And the total amount displayed matches the sum of all INR to JPY transactions
    And audit trails exist for all displayed transactions

  Scenario: Verify no transactions other than INR to JPY appear when filter applied
    When the user is on the transaction history page
    And applies currency filter from "INR" to "JPY"
    Then the transaction list contains no currencies other than INR to JPY pairs

  Scenario: Verify real-time updates reflect correct INR to JPY filtering
    Given the user is viewing transaction history with filter from "INR" to "JPY" applied
    When a new INR to JPY conversion transaction is approved in real-time
    Then the new transaction appears in the filtered list automatically
    And audit trail logs the approval action with timestamp and user

  Scenario Outline: Role-based approval restrictions enforced on filter results
    Given a user "<username>" with role "<role>" is logged in
    When the user filters currency conversion transactions from "INR" to "JPY"
    Then displayed transactions have approval statuses within user role "<role>" permissions

    Examples:
      | username       | role            |
      | roleBasedUser1 | APPROVER_LEVEL_1|
      | roleBasedUser2 | APPROVER_LEVEL_2|

