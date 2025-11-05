# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7207
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:14:17
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Handle empty state for INR to JPY transactions

  Scenario: No INR to JPY transactions exist
    Given the user is logged in
    And there are no previous INR to JPY transactions
    When I navigate to the transaction history page
    And I filter to INR to JPY conversion
    Then I should see a message 'No INR to JPY transactions found'
    And I should not see any erroneous or unrelated transactions
