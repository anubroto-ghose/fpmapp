# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7219
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:09:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Filter FPM records

  Scenario: Successful filtering of FPM records by status
    Given the user is logged in
    And the user is on the FPM records page
    When the user clicks on the status filter dropdown
    And the user selects an active status from the dropdown
    And the user clicks on the Apply button to filter records
    Then the FPM records displayed on the page should only show records with the selected status
