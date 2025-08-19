# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4861
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:18:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Status Tracking for Submissions

  Scenario: Verify visibility of status tracking at all stages
    Given the requester has made at least one submission and is logged into the system
    When the requester navigates to the submissions page
    Then the status tracking section should be visible
    And the status should accurately reflect the current stage of the submission
