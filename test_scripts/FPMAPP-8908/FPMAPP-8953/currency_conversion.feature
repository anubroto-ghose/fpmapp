# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8953
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:38:10
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: INR to JPY Currency Conversion Display
  As a financial user
  I want to verify that INR to JPY currency conversion entries are displayed correctly
  So that I can trust the transaction history data and approval statuses

  Background:
    Given the role-based approval feature is active
    And real-time currency integration is enabled
    And at least one INR to JPY transaction exists in the system

  Scenario: Verify correct data is displayed for INR to JPY conversion entry
    When I navigate to the transaction history page
    Then I should see at least one INR to JPY conversion entry
    And the date should be displayed in "DD-MM-YYYY" format
    And the amount in INR should be displayed correctly with currency symbol
    And the amount in JPY should be correctly calculated using real-time rates
    And the status should be displayed as "Completed"
    And the approval status should be shown correctly
    And no data corruption or mismatch should be present
