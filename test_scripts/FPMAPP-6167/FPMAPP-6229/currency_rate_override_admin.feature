# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6229
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:33:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin overrides currency rates with audit logging and alert email
  As a financial admin
  I want to override currency rates in real-time
  So that audit logging and alerting of overrides are reliably performed

  Background:
    Given an admin user with ID "admin123" is authorized
    And the currency code "EUR" exists in the system
    And the SMTP server is configured and functional

  Scenario: Successful admin override of EUR currency rate with audit log and alert
    When the admin user sends a POST request to "/currency/rates/override" with:
      | currencyCode   | overriddenRate | overrideReason      | adminUserId |
      | EUR            | 1.15           | Quarterly adjustment | admin123    |
    Then the API responds with success: true
    And the response contains a non-empty overrideId
    And the currency rate for "EUR" is updated with:
      | overriddenRate | overrideFlag | adminUserId | overrideReason      |
      | 1.15           | true         | admin123    | Quarterly adjustment |
    And an audit log entry exists for the override with:
      | userId   | actionType             | remarks               |
      | admin123 | CURRENCY_RATE_OVERRIDE | Quarterly adjustment |
    And an alert email is sent to configured recipients containing the override details
