# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8952
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:39:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History for INR to JPY Currency Conversions
  
  Background:
    Given the user "testuser" is logged into the FPM Tools application with role-based approval enabled
    And the user has performed at least one INR to JPY currency conversion in the past

  Scenario: View transaction history page filtered by INR to JPY
    When the user navigates to the transaction history page
    And the user selects the currency pair "INR to JPY"
    Then the user should see a list of all previous INR to JPY conversion entries
    And each entry should display the date of transaction
    And each entry should display the amount in INR
    And each entry should display the equivalent amount in JPY
    And each entry should display the transaction status
    And the data should reflect real-time currency integration rates where applicable
    And the role-based approval status should not interfere with transaction visibility
    And there should be no regression in UI or data display from previous versions

