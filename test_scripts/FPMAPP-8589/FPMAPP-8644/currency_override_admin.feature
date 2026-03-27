# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8644
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:43:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin Override Currency Exchange Rate and Alert Email
  
  As a financial analyst with admin privileges
  I want to override currency exchange rates with a valid reason
  So that the system records the override and sends alert emails

  Background:
    Given an admin user "adminUser" with password "adminPass123" is logged in
    And the SMTP email service is configured and operational
    And the currency exchange rate for "USD" is 1.10

  Scenario: Admin successfully overrides a currency exchange rate and triggers alert email
    When the admin navigates to the currency override page
    And selects currency "USD" to override
    And enters override rate "1.15"
    And provides override reason "Quarterly adjustment due to market volatility"
    And submits the override
    Then the override is saved successfully
    And the currency exchange rate for "USD" is updated with admin override flag set to true
    And the override reason is recorded as "Quarterly adjustment due to market volatility"
    And an alert email is sent to configured recipients
    And the email content includes the currency code "USD" and override reason
    And the override action is logged for audit purposes
