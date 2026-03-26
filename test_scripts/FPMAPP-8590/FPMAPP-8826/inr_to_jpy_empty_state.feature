# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8826
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:55:56
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: INR to JPY Currency Conversion History Empty State Handling
  As a user with role-based approval enabled
  I want to see a proper empty state message when no INR to JPY conversions exist
  So that I am informed correctly and the UI remains consistent

  Background:
    Given the user is logged in with role "Manager" and role-based approval enabled
    And there are no INR to JPY conversion transactions in the user's history

  Scenario: Display empty state message when no INR to JPY conversions exist
    When the user navigates to the transaction history page
    And the user filters transactions from "INR" to "JPY"
    Then the system displays the message "No INR to JPY transactions found"
    And no unrelated transactions are shown
    And the empty state message respects role-based access and audit trail visibility
    And the UI remains consistent and responsive
