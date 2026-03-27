# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8640
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:45:58
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Override Currency Exchange Rates Validation
  As a system administrator
  I want to ensure that overriding currency exchange rates without providing an override reason
  results in a validation error and no side effects

  Background:
    Given the user is authenticated as an admin with valid API access
    And the Currency_Exchange_Rates table is accessible

  Scenario: Override attempt with missing overrideReason should fail with validation error
    When the admin sends a PUT request to "/fpm/currency/rates" with body:
      | overrideFlag |
      | true         |
    And the overrideReason is omitted
    Then the API response status should be 400 Bad Request
    And the response should contain a validation error indicating "overrideReason is required when overrideFlag is true"
    And no changes should be made to the Currency_Exchange_Rates table
    And no alert emails should be sent
    And no audit log entry should be created
