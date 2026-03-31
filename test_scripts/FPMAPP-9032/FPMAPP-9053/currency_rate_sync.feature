# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9053
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:47:28
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Synchronization
  As a finance admin
  I want to synchronize real-time currency exchange rates with historical rate queries and admin override capabilities
  So that the financial application has accurate and up-to-date currency data

  Background:
    Given the third-party currency exchange rate provider API is available and responsive
    And the Fpm service synchronization job is configured and enabled

  Scenario: Successful synchronization of currency exchange rates
    When I trigger the synchronization manually via POST "/api/fpm/currency/rates/sync"
    Then the API returns a success status indicating synchronization completed
    And the database contains updated current exchange rates
    And historical exchange rate data is stored with accurate timestamps
    And no errors or exceptions occur during synchronization
