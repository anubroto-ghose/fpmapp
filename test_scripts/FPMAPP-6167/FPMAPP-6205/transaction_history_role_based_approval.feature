# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6205
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:51:43
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History Access with Role-based Approval and Audit Trail
  
  Background:
    Given the system has currency conversion history including transactions between INR and JPY
    And the user "testuser" with roles "ROLE_APPROVER" and "ROLE_USER" is logged into the system

  Scenario: User views INR to JPY conversion transaction history with role-based access and audit trails
    When the user navigates to the transaction history page
    And filters the transactions for currency pair From "INR" To "JPY"
    Then the user should see a list of INR to JPY conversion transactions permitted by role
    And each transaction entry should display:
      | Date of transaction      |
      | Amount in INR            |
      | Equivalent amount in JPY |
      | Transaction status       |
    And audit trail indicators should be visible for transactions with audit data
    When the user views the audit trail for a transaction
    Then the user should see timestamped approval, rejection, and delegation actions

  Scenario: User with insufficient role permissions cannot view restricted transaction data
    Given the user "limiteduser" with role "ROLE_USER" is logged into the system
    When the user navigates to the transaction history page
    And filters the transactions for currency pair From "INR" To "JPY"
    Then the user should only see transactions permitted by their roles
    And restricted transactions should not be visible or accessible

  Scenario: Real-time currency integration does not affect historical transaction display
    When the user views historical INR to JPY transactions
    Then the transaction data should remain consistent despite realtime currency updates

  Scenario: Transaction history page maintains backward compatibility with existing UI and data
    When the user accesses the transaction history page without filters
    Then all historical transactions visible prior to role-based approvals should be displayed correctly
    And the UI layout remains consistent with previous versions
