# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8882
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:36:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin Override of Currency Exchange Rates
  As a financial analyst with admin override permissions
  I want to override currency exchange rates manually
  So that the system uses the overridden rates and logs the action with alerts

  Background:
    Given an admin user "adminUser" with override permissions is logged into the system
    And exchange rates exist in the system
    And alerting and audit logging systems are operational

  Scenario: Admin successfully overrides the exchange rate for USD/EUR
    When the admin navigates to the currency exchange rate override interface
    And the admin selects the currency pair "USD/EUR"
    And the admin changes the exchange rate to "0.90"
    And the admin saves the override
    Then the override is accepted and stored correctly
    And the override action is logged with user details and timestamp
    And an alert is generated and visible to appropriate users
    And subsequent API calls reflect the overridden exchange rate
    And no system errors occur during the override process
