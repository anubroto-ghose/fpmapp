# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8954
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:37:19
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: INR to JPY Currency Conversion Empty State Handling
  
  As a logged-in user with role-based approval enabled
  I want to see a proper empty state message when no INR to JPY conversions exist
  So that I am informed clearly and the UI remains consistent and error-free

  Background:
    Given the user "user123" is logged in with role-based approval enabled
    And there are no previous INR to JPY transactions in the user's history

  Scenario: Display empty state message when no INR to JPY conversions exist
    When the user navigates to the transaction history page
    And filters transactions from "INR" to "JPY"
    Then the system displays the message "No INR to JPY transactions found"
    And no erroneous or unrelated transactions are shown
    And no error messages or misleading information are displayed
    And the UI remains consistent and user-friendly
