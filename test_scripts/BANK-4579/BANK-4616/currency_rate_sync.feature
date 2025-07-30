# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4616
# Epic: BANK-4579
# Generated on: 2025-07-30 05:02:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Sync of Currency Rates

  Scenario: Successful manual sync of currency rates
    Given the admin is logged into the system
    When the admin navigates to the currency sync section
    And clicks on the "Sync Currency Rates" button
    Then the system should log the sync action with a timestamp
    And the latest currency rates should be updated in the database
    And the admin should receive a notification confirming the successful sync
