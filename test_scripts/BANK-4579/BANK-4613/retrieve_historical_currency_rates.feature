# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4613
# Epic: BANK-4579
# Generated on: 2025-07-30 05:05:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Retrieve Historical Currency Rates

  Scenario: Successful retrieval of historical currency rates
    Given the user is logged into the system with appropriate permissions
    When the user navigates to the historical currency rates section
    And the user specifies valid criteria for retrieving historical rates
      | Currency Pair | Start Date | End Date   |
      | USD/EUR      | 2023-01-01 | 2023-01-31 |
    And the user clicks on the 'Retrieve Rates' button
    Then the system should display the historical currency rates in a user-friendly format