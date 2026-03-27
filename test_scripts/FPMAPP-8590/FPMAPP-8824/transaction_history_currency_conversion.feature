# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8824
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:46:39
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History for INR to JPY Currency Conversions
  As a logged-in user with role-based approval enabled
  I want to view the transaction history page filtered for INR to JPY conversions
  So that I can verify all past transactions and their audit trails

  Background:
    Given the user "testuser" is logged into the system with role-based approval enabled
    And the user has performed at least one INR to JPY conversion in the past with audit trail recorded

  Scenario: View transaction history filtered by INR to JPY
    When the user navigates to the transaction history page
    And the user filters the transactions by currency pair "INR" to "JPY"
    Then the user should see a list of all previous INR to JPY conversion entries
    And each entry should display the date of transaction
    And each entry should display the amount in INR
    And each entry should display the equivalent amount in JPY
    And each entry should display the transaction status
    And each entry should display accurate audit trail information
    And the data should be consistent with backend records
    And role-based approval integration should not affect transaction visibility
