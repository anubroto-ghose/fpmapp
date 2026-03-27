# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8624
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:57:20
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Synchronization
  
  As a financial analyst
  I want real-time synchronization of currency exchange rates with historical query support
  So that I can rely on up-to-date and accurate currency data for financial planning

  Background:
    Given the external currency exchange rate provider is available and responding
    And the scheduled synchronization job is configured and enabled

  Scenario: Successful scheduled synchronization updates currency rates
    When the scheduled synchronization job runs
    Then the synchronization job completes successfully without errors
    And the latest currency exchange rates are fetched from the external provider
    And the fetched rates are stored in the database with correct timestamps
    And no data loss or corruption occurs during synchronization

  Scenario: Synchronization job logs the process correctly
    When the scheduled synchronization job runs
    Then the synchronization logs show successful completion
    And no error messages are present in the logs
