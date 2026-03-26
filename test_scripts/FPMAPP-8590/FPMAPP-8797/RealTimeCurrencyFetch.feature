# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8797
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:34:13
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Fetching
  As a financial analyst
  I want the system to fetch real-time currency exchange rates automatically at configured intervals
  So that I can rely on up-to-date currency data without manual intervention

  Background:
    Given the system is configured with a valid third-party currency exchange API
    And the fetch interval is set to 5 minutes
    And the system scheduler service is running

  Scenario: System automatically fetches real-time currency rates at configured intervals
    When the system starts
    Then the system should fetch real-time currency rates automatically every 5 minutes
    And each fetch event should be logged with a timestamp
    And no errors or failures should occur during the fetch process
    And the fetched currency data should be stored correctly in the database

  Scenario Outline: Verify multiple fetch events over time
    Given the system has been running for more than <duration> minutes
    When I check the currency fetch audit logs
    Then there should be at least <expectedFetches> successful fetch events logged
    And no error events should be present in the logs

    Examples:
      | duration | expectedFetches |
      | 10       | 2               |
      | 15       | 3               |
