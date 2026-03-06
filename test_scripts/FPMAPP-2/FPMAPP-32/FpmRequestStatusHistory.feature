# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-32
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:27:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Request Status History
  As a requester
  I want to view the status history of my requests
  So that I can track the approval process

  Scenario: View status history of a submitted request
    Given I am logged in as a requester
    When I navigate to the 'My Requests' section
    And I select a request from the list
    And I click on the 'View Status History' option
    Then I should see the history of all status changes for the request
    And the history should display timestamps and previous statuses