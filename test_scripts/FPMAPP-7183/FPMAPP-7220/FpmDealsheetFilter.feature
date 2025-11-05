# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7220
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:09:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Filter FPM records by status

  Scenario: Filtering with no results returned
    Given the user is logged in
    And the user navigates to the FPM records page
    When the user clicks on the status filter dropdown
    And the user selects a status with no records associated
    And the user clicks on the Apply button
    Then an error message should be displayed indicating that no records were found for the selected filter