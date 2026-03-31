# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8928
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:01:32
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Automatic Currency Exchange Rate Synchronization
  As a financial analyst
  I want the system to automatically fetch and update currency exchange rates at configured intervals
  So that I can rely on up-to-date exchange rates without manual intervention

  Background:
    Given the system is connected to the third-party currency exchange API
    And the sync interval is configured and active
    And the user has access to view currency exchange rates

  Scenario: Verify automatic fetching and updating of currency exchange rates
    When the configured sync interval elapses
    Then the system should automatically fetch new currency exchange rates
    And the currency exchange rates in the system should be updated
    And the system logs should show successful sync job execution
    And no data inconsistencies or delays should be observed
