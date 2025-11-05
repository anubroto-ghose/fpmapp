# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7205
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:15:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: View Transaction History

  Scenario: User views transaction history for INR to JPY conversions
    Given the user is logged into the system
    And the user has previously performed INR to JPY conversions
    When the user navigates to the transaction history page
    And the user filters transactions by currency pair "INR to JPY"
    Then the user should see a list of previous INR to JPY conversion entries
    And the details of each entry should include:
      | Field               | Expected Value     |
      | Date of transaction | 2025-11-01        |
      | Amount in INR      | 1000              |
      | Equivalent amount in JPY | 7200       |
      | Transaction status  | Completed         |
    And the next entry should include:
      | Date of transaction | 2025-11-02        |
      | Amount in INR      | 500               |
      | Equivalent amount in JPY | 3600       |
      | Transaction status  | Completed         |