# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-32
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:44:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: View Status History

  Scenario: Check history of status changes for requests
    Given the requester has submitted a request and is logged into the system
    When the requester navigates to the 'My Requests' section
    And selects a request from the list
    And clicks on the 'View Status History' option
    Then a history of all status changes for the request is displayed, showing timestamps and previous statuses
