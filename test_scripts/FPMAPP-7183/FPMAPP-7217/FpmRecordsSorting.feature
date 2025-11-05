# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7217
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:10:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Sort FPM Records by Customer

  Scenario: Successful sorting of FPM records by customer
    Given User is logged in as a portfolio analyst
    And FPM records are displayed in the record list
    When User locates the sorting options beside the customer column
    And User clicks on the sorting option for customer
    Then The records should be sorted in ascending order by customer
