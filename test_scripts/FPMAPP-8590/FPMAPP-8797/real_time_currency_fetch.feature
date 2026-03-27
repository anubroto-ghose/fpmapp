# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8797
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:04:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Rate Fetching
  As a financial analyst
  I want the system to fetch real-time currency exchange rates automatically at configured intervals
  So that I can rely on up-to-date currency data without manual intervention

  Background:
    Given the system is configured with a valid third-party currency exchange API
    And the fetch interval is set to 5 minutes
    And the system clock and scheduler service are operational

  Scenario: System automatically fetches real-time currency rates at configured intervals
    When the system starts and the scheduler service is running
    Then the system should fetch real-time currency rates automatically every 5 minutes
    And each fetch event should be logged with a timestamp
    And no errors or failures should occur during the fetch process
    And the fetched currency data should be stored correctly in the database

  Scenario: Verify logs contain successful fetch events
    Given the system has been running for more than 10 minutes
    When I view the system logs
    Then I should see multiple successful currency fetch events logged with timestamps
    And I should not see any error or failure logs related to currency fetching
