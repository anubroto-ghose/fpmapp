# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8609
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:07:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rate Synchronization
  
  As a financial analyst
  I want real-time currency exchange rate synchronization with historical rate queries
  So that I can rely on up-to-date currency data for financial planning and management

  Background:
    Given the external currency exchange rate provider is available and responsive
    And the scheduled synchronization job is configured and enabled
    And the database is accessible and ready to store currency rates

  Scenario: Successful synchronization of currency exchange rates at scheduled intervals
    When I trigger the scheduled synchronization job manually
    Then the synchronization job completes without errors
    And the latest currency exchange rates are fetched successfully
    And the currency exchange rates in the database are updated with the latest values
    And the timestamps and versioning columns reflect the update time and data version
    And no data loss or corruption occurs during synchronization
