# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6227
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:35:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-Time Currency Rate Synchronization
  As a financial admin
  I want to synchronize real-time currency exchange rates via the API
  So that the system reflects latest valid effective rates without creating overrides or audit entries

  Background:
    Given the currency rate synchronization service is configured and reachable
    And valid third-party currency API credentials are set up
    And I am an authenticated admin user

  Scenario: Successful synchronization of currency rates
    When I send a POST request to "/currency/rates/sync" endpoint
    Then the response should have "success" set to true
    And the response should contain a valid ISO 8601 "syncedAt" timestamp
    And the CurrencyRates table should contain updated rates with current effective_date
    And all updated rates should have "override_flag" set to false
    And no override audit logs should be created
    And no alert emails should be sent

  Scenario Outline: Scheduled currency synchronization triggers API correctly
    Given the scheduled job triggers the currency rate sync
    When the synchronization is performed
    Then the response should have "success" set to true
    And the CurrencyRates table should be updated

    Examples:
      | scheduledTime         |
      | 2025-09-15T05:00:00Z |
      | 2025-09-15T06:00:00Z |
