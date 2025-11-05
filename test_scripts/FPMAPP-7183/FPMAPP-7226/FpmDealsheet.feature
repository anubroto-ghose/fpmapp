# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7226
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:07:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Verify INR to JPY Conversion Display

  Scenario: Check conversion details for a valid INR to JPY transaction
    Given There is a INR to JPY transaction in the system
    When I navigate to the transaction history page
    Then I should see the conversion entry
      And the date format should be in DD-MM-YYYY
      And the amount in INR should be displayed as "₹5000"
      And the amount in JPY should be correctly calculated
      And the status should be displayed as "Completed"