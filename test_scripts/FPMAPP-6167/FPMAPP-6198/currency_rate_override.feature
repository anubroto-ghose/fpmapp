# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6198
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:57:29
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Admin Override with Audit and Email Notification
  As a financial system admin
  I want to override currency exchange rates manually
  So that the overrides are audited and alert email notifications are sent

  Background:
    Given a valid admin user with id "admin123" and role "ADMIN"
    And SMTP email service is configured and operational
    And currency code "EUR" exists in the system

  Scenario: Successful admin override of EUR currency rate
    When the admin submits a POST request to "/currency/rates/override" with body:
      | currencyCode    | overriddenRate | overrideReason                  | adminUserId |
      | EUR             | 1.2345         | Manual correction after audit  | admin123    |
    Then the API response should have status 200
    And the response JSON should contain "success" with value true
    And the response JSON should contain a non-empty "overrideId"
    And the currency rates database should have an entry for "EUR" with:
      | overrideFlag   | true                 |
      | overrideReason | Manual correction after audit |
      | adminUserId    | admin123              |
      | overrideTimestamp | a recent timestamp   |
    And an audit log entry for the override should exist with:
      | actionType      | currency_rate_override |
      | userId          | admin123              |
      | remarks         | Manual correction after audit |
      | timestamp       | a recent timestamp     |
    And an alert email should be sent to configured recipients containing:
      | currencyCode    | EUR                   |
      | overriddenRate  | 1.2345                 |
      | adminUserId    | admin123              |
      | overrideReason | Manual correction after audit |

  Scenario Outline: Fail override due to unauthorized user
    Given a user with id "<userId>" and role "<role>"
    When the user submits a POST request to "/currency/rates/override" with body:
      | currencyCode    | overriddenRate | overrideReason                  | adminUserId |
      | EUR             | 1.2345         | Manual correction after audit  | <userId>    |
    Then the API response should have status 403
    And the response JSON should contain "success" with value false

    Examples:
      | userId   | role    |
      | user123  | USER    |
      | guest001 | GUEST   |

  Scenario: Fail override due to non-existent currency
    Given a valid admin user with id "admin123" and role "ADMIN"
    When the admin submits a POST request to "/currency/rates/override" with body:
      | currencyCode    | overriddenRate | overrideReason             | adminUserId |
      | XYZ             | 2.50           | Attempt override invalid   | admin123    |
    Then the API response should have status 400
    And the response JSON should contain "success" with value false
    And the error message should mention "Currency code not found"
