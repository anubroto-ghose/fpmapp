# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8881
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:36:46
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Integration
  As a financial analyst
  I want the system to integrate real-time currency exchange rates with auto-sync and admin override capabilities
  So that I can rely on up-to-date and accurate currency data for financial planning

  Background:
    Given the third-party currency exchange API is accessible and operational
    And the system is configured with valid API credentials and a sync interval of 5 seconds
    And the database is accessible and ready to store exchange rate data

  Scenario: System fetches current and historical exchange rates at configured intervals
    When the system starts and the scheduled sync job is enabled
    Then the system should call the third-party API at the configured intervals
    And current exchange rates should be retrieved without errors
    And historical exchange rates should be retrieved without errors
    And the fetched exchange rates should be stored in the database with accurate timestamps
    And no duplicate or missing entries should occur during sync

  Scenario: Admin overrides the currency exchange rate sync
    Given the system has fetched exchange rates
    When the admin triggers a manual sync override
    Then the system should immediately fetch current and historical exchange rates from the API
    And the database should be updated with the latest exchange rates and timestamps
    And no duplicate entries should be created
