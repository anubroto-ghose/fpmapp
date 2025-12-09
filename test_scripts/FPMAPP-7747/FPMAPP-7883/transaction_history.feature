# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7883
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:17:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Transaction History

  Scenario: No INR to JPY transactions found
    Given the user is logged in
    And there are no previous INR to JPY transactions in their history
    When the user navigates to the transaction history page
    And filters to INR to JPY conversion
    Then a message "No INR to JPY transactions found" should be displayed
    And no erroneous or unrelated transactions should be shown