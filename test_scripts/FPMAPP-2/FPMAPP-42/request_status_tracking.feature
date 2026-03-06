# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-42
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:30:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Request Status Tracking

  Scenario: Verify real-time status visibility for requests
    Given the requester is logged into the application
    And the requester has at least one request submitted
    When the requester navigates to the 'My Requests' section
    Then the current status of the requests should be visible
    When the requester refreshes the page
    Then any changes in status should be reflected immediately upon refresh