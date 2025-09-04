# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5258
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:19:56
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin Override Logging
  As a system administrator
  I want the system to log currency rate overrides
  So that audit trail and notifications are accurate

  Background:
    Given the administrator has already overridden the currency rate "USD" from 1.00 to 1.15

  Scenario: Validate logging of admin override actions
    When the administrator navigates to the logs section
    And searches for logs related to the recent currency override action
    Then the logs should reflect the recent currency override action
    And the logs should include admin ID "admin123"
    And the logs should include currency code "USD"
    And the logs should include old rate "1.00"
    And the logs should include new rate "1.15"
    And the logs should include a valid timestamp
