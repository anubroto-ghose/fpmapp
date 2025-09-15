# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6218
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:41:09
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Exchange Rate Retrieval with Synchronization Verification
  
  As a finance administrator
  I want to retrieve historical currency exchange rates
  So that I can verify rates, overrides and synchronization job activity

  Background:
    Given the currency exchange rate synchronization job is configured and running
    And historical currency data exists covering previous dates
    And I have API access to "GET /api/fpm/currency/rates"

  Scenario: Retrieve historical currency rates within a valid date range
    When I request currency rates with date range "2025-09-10" to "2025-09-11"
    Then the response should contain currency codes with valid rates and timestamps
    And the override flags are correctly set where applicable

  Scenario: Verify synchronization job last run status
    When I query the currency synchronization job status
    Then the last run should be successful and recent within 2 days

  Scenario: Verify no errors or missing data in historical currency rates response
    When I request currency rates with date range "2025-09-10" to "2025-09-11"
    Then no errors should be present in the response
    And data should not be missing for expected currencies "USD", "EUR", "JPY"
