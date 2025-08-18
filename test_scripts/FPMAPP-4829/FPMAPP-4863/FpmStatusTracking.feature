# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4863
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:18:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Status Tracking Visibility

  Scenario: Verify status tracking visibility for requesters
    Given the user is logged in as a requestor
    And the user has submitted a request
    When the user navigates to the submissions page
    Then the submitted request should be visible
    And the status tracking section should be visible
    And the current status of the request should be displayed
