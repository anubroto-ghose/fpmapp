# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8825
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:55:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: INR to JPY Currency Conversion Regression
  As a user of the FPM application
  I want to verify that INR to JPY currency conversion entries display correct data
  So that I can trust the financial transaction history and audit trail

  Background:
    Given the system has at least one INR to JPY transaction with role-based approval and audit trail enabled

  Scenario: Verify correct data is displayed for each INR to JPY conversion entry
    When I navigate to the transaction history page
    Then I should see at least one INR to JPY conversion entry
    And the date should be displayed in "DD-MM-YYYY" format
    And the amount in INR should be displayed correctly with currency symbol
    And the amount in JPY should be correctly calculated using real-time currency integration
    And the status should be displayed as "Completed"
    And the audit trail details should be accessible and accurate

  # Step Definitions (for reference, to be implemented in Java)
  # Given the system has at least one INR to JPY transaction with role-based approval and audit trail enabled
  #   - Setup test data or mock backend to ensure transaction exists
  # When I navigate to the transaction history page
  #   - Use Selenium WebDriver to open the page
  # Then I should see at least one INR to JPY conversion entry
  #   - Verify presence of table row with INR->JPY
  # And the date should be displayed in "DD-MM-YYYY" format
  #   - Parse and validate date format
  # And the amount in INR should be displayed correctly with currency symbol
  #   - Check INR amount formatting
  # And the amount in JPY should be correctly calculated using real-time currency integration
  #   - Validate JPY amount matches INR * conversion rate
  # And the status should be displayed as "Completed"
  #   - Check status text
  # And the audit trail details should be accessible and accurate
  #   - Open audit trail modal and verify entries
