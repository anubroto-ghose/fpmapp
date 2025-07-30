# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4618
# Epic: BANK-4579
# Generated on: 2025-07-30 05:01:02
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Sync of Currency Rates

  Scenario: Sync currency rates when the external API is down
    Given the admin is logged in
    When the admin navigates to the currency sync section
    And clicks on the "Sync Currency Rates" button
    Then an error message should be displayed indicating that the sync could not be completed due to the API being unavailable
    And the system should log the sync attempt with a timestamp
