# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8813
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:53:39
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Authorization
  As a user without currency admin privileges
  I want to be prevented from overriding currency exchange rates
  So that unauthorized changes are not made to financial data

  Background:
    Given the user "unauthorizedUser" is logged in with role "USER"
    And the currency override API endpoint "/api/currency/override" is accessible

  Scenario: Unauthorized user attempts to submit a currency override
    When the user submits a currency override request with new exchange rate 1.25 and reason "Test override attempt by unauthorized user"
    Then the override request is rejected with an authorization error
    And no new override log entry is created
    And no alert is generated for the unauthorized attempt
    And the currency data remains unchanged
