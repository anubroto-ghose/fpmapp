# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8942
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:48:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin Override of Currency Rates
  
  As a financial analyst with admin privileges
  I want to override currency exchange rates with a valid reason
  So that the system logs the override and alerts stakeholders

  Background:
    Given an admin user "adminUser" is authenticated
    And currency rate data exists for "USD" with rate 1.0
    And the alerting system is operational

  Scenario: Admin successfully overrides a currency rate with reason logging and alert triggering
    When the admin submits an override request for currency "USD" with rate 1.15 and reason "Quarterly adjustment due to market volatility"
    Then the currency rate for "USD" is updated with admin override flag set to true
    And the override reason "Quarterly adjustment due to market volatility" and timestamp are logged
    And an alert is triggered notifying stakeholders of the override
    And querying the API for currency "USD" returns the overridden rate with override status and timestamps

  Scenario: Unauthorized user attempts to override currency rate
    Given a non-admin user "normalUser" is authenticated
    When the user attempts to override currency "USD" rate to 1.20 with reason "Unauthorized attempt"
    Then the override request is rejected with an authorization error
    And the currency rate for "USD" remains unchanged
