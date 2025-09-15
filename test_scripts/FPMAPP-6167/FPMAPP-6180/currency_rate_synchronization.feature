# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6180
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:12:09
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Scheduled Currency Rate Synchronization and Override Alerts
  As a finance administrator
  I want scheduled synchronization jobs to update currency exchange rates in real-time and historical databases
  So that currency data is accurate and overrides/failures are logged and notified

  Background:
    Given the scheduled currency synchronization job is configured and enabled
    And access to the external third-party currency data source is available
    And the database is accessible

  @happyPath
  Scenario: Scheduled job runs and updates currency rates successfully
    When the scheduled synchronization job executes
    Then the job status should be "success"
    And the currency rates in the database should reflect the latest data from the third-party source
    And historical currency rates should be preserved correctly
    And no synchronization failure alerts should be present

  @failureScenario
  Scenario: Scheduled job fails to retrieve rates and triggers alert
    Given the external currency service is unavailable
    When the scheduled synchronization job executes
    Then the job status should be "failed"
    And an error alert for synchronization failure should be generated
    And historical currency rates should remain unchanged

  @dataVerification
  Scenario Outline: Verify real-time and historical currency rates after synchronization
    Given the following historical rates are stored in the database:
      | currencyCode | rate | timestamp           |
      | EUR          | 0.80 | <historicalDate>    |
    When the scheduled synchronization job executes
    Then the database should contain the latest real-time rate for "USD" with rate 1.00
    And the database should contain the historical rate for "EUR" with rate 0.80

    Examples:
      | historicalDate           |
      | 2025-09-10T10:00:00     |
