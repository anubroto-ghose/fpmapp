# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8813
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:46:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Authorization
  As a user without currency admin privileges
  I want to be prevented from submitting currency override requests
  So that unauthorized overrides are rejected and logged appropriately

  Background:
    Given the currency override API endpoint "/api/currency/override" is available
    And a user is logged in with role "ROLE_USER" (not authorized for overrides)

  Scenario: Unauthorized user attempts to submit a currency override
    When the user submits a currency override request with:
      | currencyPair | USD/EUR |
      | newRate     | 0.85    |
      | reason      | Test override attempt by unauthorized user |
    Then the API response status should be 403
    And the response message should contain "unauthorized"
    And no new override log entry should be created
    And no alert should be generated for the unauthorized attempt
    And the currency data should remain unchanged
