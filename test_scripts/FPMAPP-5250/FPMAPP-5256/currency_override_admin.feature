# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5256
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:55:52
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Override by Admin
  As a system administrator
  I want to override currency exchange rates with notifications and audit logs
  So that I can maintain accurate and up-to-date currency rates with full traceability

  Background:
    Given the system administrator is logged in

  Scenario: Successful currency rate override
    When the administrator navigates to the currency rate management section
    And selects the currency "USD"
    And enters an override rate of 1.25
    And submits the override
    Then the currency rate should be updated successfully
    And an override alert should be displayed
    And the override action should be logged in the audit trail
