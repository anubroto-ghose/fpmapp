# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8639
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:46:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin Override of Currency Exchange Rates
  As a system administrator
  I want to override currency exchange rates with alerting and audit logging
  So that financial data reflects market adjustments accurately and stakeholders are notified

  Background:
    Given the user is authenticated as an admin with valid API access
    And the SMTP mail server is configured and reachable
    And the Currency_Exchange_Rates table contains current rates

  Scenario: Successful override of currency exchange rate with valid admin credentials and alerting
    When the admin sends a PUT request to "/fpm/currency/rates" with body:
      | overrideFlag | overrideReason    |
      | true         | Market adjustment |
    Then the API response confirms the override with an audit log reference
    And the Currency_Exchange_Rates table is updated with overrideFlag set to true and overrideReason set to "Market adjustment"
    And an alert email is sent to the relevant stakeholders with correct override details
    And the currency rate API reflects the overridden rate immediately
    And the currency rate UI displays the overridden rate immediately
    And the audit logs contain the override action with user ID, timestamp, and reason

