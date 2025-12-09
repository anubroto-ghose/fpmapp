# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7881
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:19:13
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History

  Scenario: Access transaction history for INR to JPY conversions
    Given the user is logged into the system
    And the user has performed at least one INR to JPY conversion in the past
    When the user navigates to the transaction history page
    And the user filters the currency pair as "INR to JPY"
    Then the user should see a list of all previous INR to JPY conversion entries
    And each entry should display:
      | Field                |
      | Date of transaction  |
      | Amount in INR       |
      | Equivalent amount in JPY |
      | Transaction status   |