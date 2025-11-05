# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7215
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:11:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Return to FPM record list
  In order to maintain my place in the FPM records
  As a portfolio analyst
  I want to return to the FPM record list without losing my position

  Scenario: User navigates back to FPM records list
    Given the user is on the detailed view page of an FPM record
    When the user clicks the return button
    Then the user should be taken back to the FPM records list
    And the user should be positioned at the same FPM record that was clicked earlier