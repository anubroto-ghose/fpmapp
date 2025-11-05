# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7227
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:07:02
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM Dealsheet Conversion History

  Scenario: Handle empty state when no INR to JPY conversions exist
    Given the user is logged in
    And there are no previous INR to JPY transactions
    When the user navigates to the transaction history page
    And filters to INR to JPY conversion
    Then a message "No INR to JPY transactions found" is displayed
    And no erroneous or unrelated transactions are shown
