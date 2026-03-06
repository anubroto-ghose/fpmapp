# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-31
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:27:02
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time status updates for requests

  Scenario: Validate real-time status updates for requests
    Given the requester has submitted a request and is logged into the system
    When the requester navigates to the 'My Requests' section
    And selects a request from the list
    And waits for a status update to occur
    Then the status of the request should update in real-time without needing to refresh the page manually
