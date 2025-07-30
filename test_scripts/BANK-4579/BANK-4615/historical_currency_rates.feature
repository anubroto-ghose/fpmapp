# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4615
# Epic: BANK-4579
# Generated on: 2025-07-30 05:03:19
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: View Historical Currency Rates

  Scenario: Filter historical currency rates by date range
    Given the user is logged into the system
    When the user navigates to the historical currency rates section
    And the user specifies a valid date range from "2023-01-01" to "2023-01-31"
    And the user clicks on the "Filter Rates" button
    Then the system should display the historical currency rates filtered by the specified date range in a user-friendly format
    And the rates should include entries for "2023-01-01" and "2023-01-31"