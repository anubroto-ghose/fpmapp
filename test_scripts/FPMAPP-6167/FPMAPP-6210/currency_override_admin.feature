# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6210
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:48:05
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin overrides currency exchange rates with audit logging and notifications
  
  As a finance administrator
  I want to override currency exchange rates via an API
  So that the override is applied, logged in audit, and alert notifications are sent

  Background:
    Given the override API endpoint is deployed and reachable
    And a finance administrator is authenticated with override permissions
    And the SMTP server for alerts is configured

  Scenario: Submit a currency rate override and verify audit and alert
    When the administrator submits an override request for currency code "USD" with new rate 1.25 and reason "Test override"
    Then the override API should return success status with updated rate details
    And an audit log entry is created capturing override details including user and timestamp
    And an alert email notification is sent to configured recipients
    And subsequent currency rate fetch returns the overridden value
