# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7882
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:18:35
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Conversion

  Scenario: Verify correct data is displayed for each INR to JPY conversion entry
    Given I have at least one INR to JPY transaction in the system
    When I navigate to the transaction history page
    Then I should see the INR to JPY conversion entry
      And the date should be in the format DD-MM-YYYY
      And the amount in INR should be displayed as ₹5000
      And the amount in JPY should be correctly calculated
      And the status should be displayed as Completed
