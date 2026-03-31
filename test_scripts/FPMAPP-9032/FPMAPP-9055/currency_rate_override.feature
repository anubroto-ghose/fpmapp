# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9055
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:45:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin override of currency exchange rate with logging and email alert
  
  As a finance admin
  I want to override currency exchange rates in real-time
  So that I can ensure accurate financial calculations and notify stakeholders

  Background:
    Given the SMTP mail server is configured and operational
    And the override API endpoint is accessible
    And an admin user with ID "admin123" exists with valid credentials

  Scenario: Successful override of currency exchange rate
    When the admin user "admin123" sends a POST request to "/api/fpm/currency/rates/override" with:
      | currency_pair | USD/EUR       |
      | new_rate      | 0.85          |
      | effective_date| <tomorrow>    |
      | admin_user_id | admin123      |
    Then the API response should confirm success
    And the override record should be stored with:
      | currency_pair | USD/EUR       |
      | rate         | 0.85          |
      | effective_date| <tomorrow>    |
      | admin_user_id | admin123      |
    And an email alert should be sent to configured recipients
    When the admin user queries the overridden rate for "USD/EUR" on <tomorrow>
    Then the overridden rate returned should be 0.85

  Scenario: Unauthorized override attempt
    When a non-admin user "unauthorizedUser" attempts to override the currency rate with:
      | currency_pair | USD/GBP       |
      | new_rate      | 0.75          |
      | effective_date| <today>       |
      | admin_user_id | unauthorizedUser |
    Then the API response should be forbidden

  @examples
  Examples:
    | today       | tomorrow    |
    | ${TODAY}    | ${TOMORROW} |

  
  # Step Definitions (for reference, not part of feature file):
  # Given the SMTP mail server is configured and operational
  # Given the override API endpoint is accessible
  # Given an admin user with ID "admin123" exists with valid credentials
  # When the admin user "admin123" sends a POST request to "/api/fpm/currency/rates/override" with:
  # Then the API response should confirm success
  # And the override record should be stored with:
  # And an email alert should be sent to configured recipients
  # When the admin user queries the overridden rate for "USD/EUR" on <date>
  # Then the overridden rate returned should be <rate>
  # When a non-admin user "unauthorizedUser" attempts to override the currency rate with:
  # Then the API response should be forbidden
