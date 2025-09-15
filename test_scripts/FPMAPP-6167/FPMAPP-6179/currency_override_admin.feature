# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6179
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:12:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Administrative Override
  As a finance administrator
  I want to override currency exchange rates through a secure API
  So that audit logs are recorded and alert emails are sent

  Background:
    Given the SMTP mail server is configured and operational
    And the currency exchange rate system has current data
    And I am authenticated as a finance administrator with override API access

  Scenario: Submit a valid currency rate override
    When I submit a currency override request with:
      | currencyCode | USD                                    |
      | newRate      | 1.25                                   |
      | adminUserId  | adminuser01                           |
      | overrideReason | Quarterly adjustment based on FX outlook |
    Then the override API should accept the request
    And the override should be persisted with audit information for currency "USD"
    And an alert email notification should be sent immediately

  Scenario: Retrieve the overridden currency rate
    Given a currency "USD" has a rate overridden to 1.25
    When I request the current currency rate for "USD"
    Then I should receive a rate equal to 1.25

  Scenario: Audit trail contains correct override details
    Given a currency override record exists for "USD"
    When I retrieve the audit trail for currency "USD"
    Then the audit trail should include:
      | adminUserId       | adminuser01                      |
      | overrideReason    | Quarterly adjustment based on FX outlook |
      And the override timestamp should be recent
