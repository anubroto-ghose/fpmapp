# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6187
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:06:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency exchange rate synchronization and retrieval
  As a finance administrator
  I want to verify scheduled synchronization updates current and historical currency rates correctly
  So that the system's currency exchange data is reliable and up to date

  Background:
    Given the system is connected to a third-party currency exchange rate provider
    And scheduled jobs for syncing exchange rates are configured and active
    And historical exchange rate data exists in the database

  Scenario: Scheduled synchronization updates current rates
    When the scheduled synchronization job runs
    Then no synchronization errors occur
    And the current currency rates reflect the latest synchronized rates
    And the current currency rates include "USD-EUR" with a rate greater than 0
    And the current currency rates include "GBP-USD" with a rate greater than 0

  Scenario: Retrieve current currency exchange rates
    When I query the "/api/fpm/currency/rates" endpoint without query parameters
    Then I receive a list of current currency rates
    And each rate entry has a valid timestamp of today or newer
    And no override flags are set on current rates

  Scenario: Retrieve historical currency exchange rates
    Given I have a valid date range from "2025-07-01" to "2025-08-30"
    When I query the "/api/fpm/currency/rates" endpoint with the date range
    Then I receive historical currency rates matching the date range
    And each returned rate timestamp is within the requested range
    And at least one rate has an override status set to true

  Scenario: Ensure no data inconsistencies or errors during synchronization
    When the synchronization job runs
    Then no errors or data inconsistencies are reported
