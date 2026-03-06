# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-42
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:47:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Request Status Tracking
  As a requester,
  I want status tracking visibility for my requests
  So that I can see the current status at every stage of the approval process

  Scenario: Verify real-time status visibility for requests
    Given I am logged into the application as a requester
    And I have at least one request submitted
    When I navigate to the "My Requests" section
    Then I should see the status of my submitted requests
    When I refresh the page
    Then the current status of the requests should be visible
    And any changes in status should be reflected immediately upon refresh