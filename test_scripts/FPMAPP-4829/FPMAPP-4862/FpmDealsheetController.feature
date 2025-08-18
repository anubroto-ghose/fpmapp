# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4862
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:19:11
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Verify visibility of status tracking when no submissions exist

  Scenario: No submissions exist
    Given the user is logged in
    When the user navigates to the submissions page
    Then the status tracking section should indicate that there are no submissions to track
