# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6197
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:58:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Rate Retrieval

  As a financial admin
  I want to retrieve historical currency exchange rates within a valid date range
  So that I can review past currency data accurately

  Background:
    Given the historical currency data exists for currency code "USD" from "2025-01-01" to "2025-01-10"

  Scenario: Retrieve historical currency rates with valid date range
    When I request historical currency rates for currency code "USD" from "2025-01-01" to "2025-01-10"
    Then the response status code should be 200
    And the response should contain a list of daily rates
    And each daily rate should have a date and rate fields
    And the rates should be sorted chronologically
    And the rates should exactly match the stored historical data
