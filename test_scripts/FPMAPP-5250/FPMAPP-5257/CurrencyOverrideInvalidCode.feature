# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5257
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:20:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Admin
  As a system administrator
  I want to override currency rates with validation
  So that invalid currency codes are rejected with proper error messages

  Background:
    Given the admin is logged into the system with appropriate permissions

  Scenario: Attempt to override currency rate with invalid currency code
    When the admin navigates to the Currency Override Admin UI
    And the admin enters an invalid currency code "ZZZ1"
    And the admin enters a valid currency rate "1.2345"
    And the admin submits the currency override change
    Then the system displays an error message "Invalid currency code"
    And the currency rate is not updated
    And no notifications or logs are triggered due to the failed attempt
