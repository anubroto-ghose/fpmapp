# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7223
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:08:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM Records Pagination

  Scenario: User navigates through paginated FPM records
    Given the user is on the paginated listing of FPM records with multiple pages available
    When the user clicks the 'Next' button
    Then the user sees the records displayed for the next page
    When the user clicks the 'Previous' button
    Then the user sees the records return to the previous page
    When the user selects page number '3'
    Then the user should see the records displayed for page number '3'