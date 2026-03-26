# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8824
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:54:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History for INR to JPY Currency Conversions
  As a logged-in user with role-based approval enabled
  I want to view my transaction history filtered for INR to JPY conversions
  So that I can verify transaction details and audit trail information

  Background:
    Given the user is logged in with role "APPROVER_MANAGER"
    And the user has performed at least one INR to JPY conversion with audit trail recorded

  Scenario: View transaction history filtered by INR to JPY
    When the user navigates to the transaction history page
    And the user filters transactions by from currency "INR" and to currency "JPY"
    Then the transaction list should display only INR to JPY conversions
    And each transaction entry should show the date of transaction
    And each transaction entry should show the amount in INR
    And each transaction entry should show the equivalent amount in JPY
    And each transaction entry should show the transaction status
    And each transaction entry should display accurate audit trail information
    And the data should be consistent with backend records
    And role-based approval integration should not affect transaction visibility
