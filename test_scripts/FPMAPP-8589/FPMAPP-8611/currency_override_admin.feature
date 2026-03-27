# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8611
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:06:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin override of currency exchange rates with audit logging and alerting
  
  As a financial analyst
  I want to ensure that admin users can override currency exchange rates
  So that the system records audit logs and sends alert emails without disrupting ongoing synchronization jobs

  Background:
    Given the admin user "admin123" is authenticated
    And the currency override API endpoint "/fpm/currency/rates/override" is available
    And the SMTP alerting system is configured

  Scenario: Successful override of currency rate by admin
    When the admin user sends a POST request to "/fpm/currency/rates/override" with:
      | currency_pair | USD/EUR           |
      | new_rate      | 0.85              |
      | effective_date| <tomorrow_date>   |
      | admin_user_id | admin123          |
    Then the API response should confirm the override with success
    And an audit log entry should be created with the override details
    And the database should reflect the override_flag set to true
    And the overridden_by field should be recorded as "admin123"
    And an alert email should be sent to the configured recipients with override details
    And the override should not disrupt ongoing synchronization jobs or queries

  @mocked
  Scenario Outline: Admin override with various currency pairs and rates
    When the admin user sends a POST request to "/fpm/currency/rates/override" with:
      | currency_pair | <currency_pair>   |
      | new_rate      | <new_rate>        |
      | effective_date| <effective_date>  |
      | admin_user_id | admin123          |
    Then the API response should confirm the override with success
    And an audit log entry should be created with the override details
    And an alert email should be sent to the configured recipients with override details

    Examples:
      | currency_pair | new_rate | effective_date |
      | USD/GBP       | 0.75     | <tomorrow_date>|
      | EUR/JPY       | 130.50   | <tomorrow_date>|
      | AUD/USD       | 0.70     | <tomorrow_date>|

  
  # Step Definitions (to be implemented in Java)
  # Given the admin user "admin123" is authenticated
  # And the currency override API endpoint "/fpm/currency/rates/override" is available
  # And the SMTP alerting system is configured
  # When the admin user sends a POST request to "/fpm/currency/rates/override" with:
  # Then the API response should confirm the override with success
  # And an audit log entry should be created with the override details
  # And the database should reflect the override_flag set to true
  # And the overridden_by field should be recorded as "admin123"
  # And an alert email should be sent to the configured recipients with override details
  # And the override should not disrupt ongoing synchronization jobs or queries

  # Note: <tomorrow_date> placeholder should be replaced dynamically in step definitions with tomorrow's date in ISO format.
