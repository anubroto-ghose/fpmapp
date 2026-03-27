# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8626
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:56:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin override of currency exchange rates with logging and alerting
  
  As a financial analyst
  I want real-time synchronization of currency exchange rates with historical query support
  So that I can override currency rates with audit logging and receive alert notifications

  Background:
    Given an admin user with username "admin" and password "adminPass123" exists
    And the POST /fpm/currency/rates/override API is accessible
    And the SMTP alerting system is configured

  Scenario: Successfully override a currency rate
    When the admin submits a POST request to override currency rate with:
      | currency_code | USD           |
      | new_rate     | 1.15          |
      | effective_date | <tomorrow>   |
    Then the API response confirms the override with an audit log reference
    And the database records the override_flag as true
    And the override_user_id and override_timestamp are recorded correctly
    And an alert email is sent to configured recipients with override details

  Scenario Outline: Override attempt with invalid data
    When the admin submits a POST request to override currency rate with:
      | currency_code | <currency_code> |
      | new_rate     | <new_rate>      |
      | effective_date | <effective_date> |
    Then the API returns validation errors
    And no override is applied
    And no alert email is sent

    Examples:
      | currency_code | new_rate | effective_date |
      |              | 1.10     | <tomorrow>    |
      | EUR          | -0.5     | <tomorrow>    |
      | GBP          | 1.20     |               |

  @helper
  Scenario: Verify system maintains data consistency and audit trail integrity
    Given a currency override has been successfully applied for currency_code "USD"
    When querying the audit logs for currency_code "USD"
    Then the audit trail contains the override with correct metadata
    And the override_flag remains set

  
  # Step Definitions (to be implemented in Java)
  
  # Note: <tomorrow> placeholder should be replaced with the date of tomorrow in ISO format
