# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8796
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:33:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Audit Logs Retrieval
  As an administrator
  I want to retrieve currency override logs with filtering options
  So that I can review override actions with details such as admin user, timestamp, override values, and reasons

  Background:
    Given the system has at least one currency override logged
    And I have valid API credentials with audit permissions

  Scenario Outline: Retrieve override logs filtered by date range, admin user, and currency pair
    When I call the currency override logs API with start date "<startDate>", end date "<endDate>", admin user "<adminUser>", and currency pair "<currencyPair>"
    Then the API response status should be 200
    And the response should contain override logs matching the filter criteria
    And each log entry should include admin user details, timestamp, override values, and reason
    And the response format should be valid JSON

    Examples:
      | startDate   | endDate     | adminUser   | currencyPair |
      | 2026-03-19  | 2026-03-21  | adminUser1  | USD/EUR      |
      | 2026-03-20  | 2026-03-23  | adminUser2  | USD/GBP      |

  Scenario: Retrieve all override logs without filters
    When I call the currency override logs API without any filters
    Then the API response status should be 200
    And the response should contain all override logs
    And each log entry should include admin user details, timestamp, override values, and reason
    And the response format should be valid JSON

  Scenario: Retrieve override logs with filters that match no entries
    When I call the currency override logs API with start date "2025-01-01", end date "2025-01-02", admin user "nonexistentUser", and currency pair "XYZ/ABC"
    Then the API response status should be 200
    And the response should contain no override logs
    And the response format should be valid JSON
