# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-30
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:26:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Request Status Tracking

  Scenario: Verify current status visibility of requests
    Given the requester has submitted a request and is logged into the system
    When the requester navigates to the 'My Requests' section
    And selects a request from the list
    Then the current status of the request should be visible
    And the current status should accurately reflect its state in the approval process