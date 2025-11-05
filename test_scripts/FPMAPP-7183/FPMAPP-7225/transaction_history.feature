# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7225
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:07:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction history for INR to JPY conversions

  Scenario: Access the transaction history page for INR to JPY conversions
    Given the user is logged into the application
    When the user navigates to the transaction history page
    And the user selects the currency filter as "INR-JPY"
    Then the user should see a list of previous INR to JPY conversion entries
    And the list entry should contain:
      | Date          | Amount in INR | Amount in JPY | Transaction Status |
      | 2025-11-04    | 1000          | 7350          | Completed         |
