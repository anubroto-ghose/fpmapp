# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8796
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:04:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Audit Logs Retrieval
  As an administrator
  I want to retrieve currency override logs with filtering
  So that I can review override activities with details and reasons

  Background:
    Given the currency override audit API is available
    And at least one override has been submitted and logged
    And I have valid API access credentials with audit permissions

  Scenario: Retrieve override logs filtered by date range, admin user, and currency pair
    When I call the override logs retrieval API with:
      | startDate  | 2024-05-01 |
      | endDate    | 2024-05-05 |
      | adminUser  | adminUser1 |
      | currencyPair | USD/EUR  |
    Then the API response status should be 200
    And the response should contain override logs matching the filter criteria
    And each log entry should include:
      | adminUser | timestamp           | currencyPair | oldValue | newValue | reason                                   |
      | adminUser1| 2024-05-01T10:15:30Z| USD/EUR      | 0.85     | 0.87     | Quarterly adjustment due to market volatility |
    And the response data should match the entries in the Currency_Override_Logs database
    And the API response should be timely and formatted as JSON

  Scenario: Retrieve override logs with no matching entries
    When I call the override logs retrieval API with:
      | startDate  | 2024-01-01 |
      | endDate    | 2024-01-02 |
      | adminUser  | nonExistingUser |
      | currencyPair | USD/JPY  |
    Then the API response status should be 200
    And the response should contain an empty list of override logs

  Scenario: Attempt to retrieve override logs without audit permissions
    Given I have API access credentials without audit permissions
    When I call the override logs retrieval API with:
      | startDate  | 2024-05-01 |
      | endDate    | 2024-05-05 |
      | adminUser  | adminUser1 |
      | currencyPair | USD/EUR  |
    Then the API response status should be 403
    And the response should contain an error message indicating insufficient permissions
