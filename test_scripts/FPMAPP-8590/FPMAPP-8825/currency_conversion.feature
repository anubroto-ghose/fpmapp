# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8825
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:45:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Conversion Transaction History Validation
  As a financial user
  I want to verify that INR to JPY currency conversion transactions are displayed correctly
  So that I can trust the accuracy and auditability of financial data

  Background:
    Given the system has at least one INR to JPY transaction with role-based approval and audit trail enabled

  Scenario: Verify correct data is displayed for INR to JPY conversion entry
    When I navigate to the transaction history page
    Then I should see at least one INR to JPY conversion entry
    And the date should be in the format "DD-MM-YYYY"
    And the amount in INR should be displayed correctly with the "₹" symbol
    And the amount in JPY should be correctly calculated with real-time currency integration
    And the status should be displayed as "Completed"
    And the audit trail details should be accurate and accessible

  Scenario: Verify backward compatibility with previous data formats
    Given there are legacy INR to JPY transactions in the system
    When I view the transaction history page
    Then all legacy transactions should display correct date and currency formatting
    And audit trail data should be linked and displayed correctly
