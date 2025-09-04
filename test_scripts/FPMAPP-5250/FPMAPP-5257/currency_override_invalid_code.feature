# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5257
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:55:03
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Admin Override
  As a system administrator
  I want to be prevented from overriding currency rates with invalid currency codes
  So that the integrity of currency data is maintained,
  and no invalid overrides or notifications occur.

  Background:
    Given the admin user "adminUser" is logged into the system with appropriate permissions

  Scenario: Attempting to override a currency rate with an invalid currency code
    When the admin navigates to the Currency Override Admin UI component
    And the admin enters an invalid currency code "XXX" and a valid currency rate "1.2345"
    And the admin submits the override change
    Then the system displays an error message "Invalid currency code"
    And the currency rate is not updated
    And no notifications or audit logs are triggered
