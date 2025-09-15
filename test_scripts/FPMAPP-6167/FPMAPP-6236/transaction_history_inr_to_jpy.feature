# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6236
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:29:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History for INR to JPY conversions
  As a logged in user with role-based access
  I want to view all my past INR to JPY currency conversion transactions
  So that I can verify entries, statuses, and audit trails without missing data

  Background:
    Given the user "testuser" with password "testpassword" is registered
      And the user has role "ROLE_APPROVER" and "ROLE_USER"
      And the user has performed at least one INR to JPY conversion in the past

  Scenario: Access transaction history page filtered by INR to JPY
    When the user logs into the application
    And navigates to the transaction history page
    And filters transactions by currency pair "INR-JPY"
    Then the transaction list displays all transactions for "INR" to "JPY"
    And each transaction shows Date, Amount in INR, Equivalent Amount in JPY, and Status
    And the user can view detailed audit trails for any transaction
    And real-time currency integration status is displayed as up-to-date

  Scenario: Audit trail confirms role based access controls
    Given the user views a transaction in the INR to JPY transaction history
    When the user requests the audit trail for the transaction
    Then the audit trail is displayed with timestamped approval and delegation actions
    And no audit entries are hidden due to role-based restrictions

  Scenario: No missing entries compared to previous functionality
    When the user filters transactions for "INR-JPY"
    Then the count of transactions is equal or greater than the baseline transaction count before role-based access changes

  @Regression
  Scenario: Verify no degradation or inconsistency after enabling role-based approvals
    Given the user "testuser" had 5 INR to JPY conversion transactions before the update
    When the user views the transaction history page
    And filters by "INR-JPY"
    Then the user sees exactly 5 transactions displayed
    And all have appropriate approval statuses and audit trail visibility
