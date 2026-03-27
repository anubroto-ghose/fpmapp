# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8883
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:35:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Handle third-party currency API downtime and rate limit scenarios gracefully
  
  As a financial analyst
  I want the system to handle third-party API downtime or rate limit errors
  So that existing exchange rate data remains consistent and administrators are notified

  Background:
    Given the system is configured to fetch exchange rates from the third-party API
    And existing exchange rate data is present for USD to EUR

  @HighPriority
  Scenario: Scheduled sync job encounters third-party API downtime
    When the scheduled sync job is triggered to fetch exchange rates
    And the third-party API is down
    Then the system should detect the API downtime
    And the existing exchange rate data should remain unchanged
    And the failure should be logged with detailed error information
    And alerts should be sent to system administrators
    And retry or backoff mechanisms should be triggered as per configuration

  Scenario: Scheduled sync job encounters third-party API rate limit exceeded
    When the scheduled sync job is triggered to fetch exchange rates
    And the third-party API returns a rate limit exceeded error
    Then the system should detect the rate limit error
    And the existing exchange rate data should remain unchanged
    And the failure should be logged with detailed error information
    And alerts should be sent to system administrators
    And retry or backoff mechanisms should be triggered as per configuration
