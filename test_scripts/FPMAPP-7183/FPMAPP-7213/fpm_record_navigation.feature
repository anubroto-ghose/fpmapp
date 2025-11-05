# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7213
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:12:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Navigate to FPM Record Detail View

  Scenario: User navigates to detailed view of an FPM record
    Given the user is on the FPM records list page
    When the user clicks on an FPM record
    Then the user should be navigated to the detailed view of the selected FPM record
