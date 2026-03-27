# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8642
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:44:54
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Synchronization
  As a financial analyst
  I want real-time currency exchange rate synchronization with historical rate queries
  So that I can rely on up-to-date currency data for financial planning

  Background:
    Given the scheduled synchronization job is configured and enabled
    And the external currency exchange rate provider is accessible and returns valid data
    And the Currency_Exchange_Rates table is accessible

  Scenario: Verify automatic scheduled synchronization updates currency exchange rates
    When the scheduled synchronization job runs
    Then the synchronization job completes successfully without errors
    And the Currency_Exchange_Rates table contains updated rates reflecting the latest data from the external provider
    And no data inconsistencies or missing rates are observed
    And synchronization respects constraints and indexes on the table
