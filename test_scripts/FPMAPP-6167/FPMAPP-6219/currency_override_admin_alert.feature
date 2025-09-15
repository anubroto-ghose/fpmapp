# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6219
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:40:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin currency override triggers alert and logging
  As a finance administrator
  I want to submit currency override requests
  So that the system logs audit info and sends alert emails

  Background:
    Given an admin user with id 9999
    And SMTP email service is configured and operational
    And audit logging is enabled

  Scenario: Successful currency override triggers audit and alert email
    Given the following currency override request details:
      | currencyCode | USD |
      | newRate      | 1.15 |
      | adminUserId  | 9999 |
      | overrideReason | Quarterly adjustment for market volatility |

    When the admin submits a POST request to "/api/fpm/currency/override" with the override details

    Then the API response status should be 200
    And the API response body should confirm "Override recorded successfully"
    And the currency rates table should reflect:
      | currencyCode | USD |
      | overridden_rate | 1.15 |
      | overridden_by_user_id | 9999 |
      | override_status | OVERRIDDEN |
      | override_reason | Quarterly adjustment for market volatility |

    And the audit log entry should contain:
      | user_id | 9999 |
      | action_type | CURRENCY_OVERRIDE |
      | remarks | contains USD and 1.15 |
    
    And an alert email should have been sent and logged
