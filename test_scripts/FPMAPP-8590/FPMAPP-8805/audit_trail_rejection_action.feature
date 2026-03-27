# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8805
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:58:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Rejection Action Logging
  As a compliance officer
  I want a full audit trail of all rejection actions
  So that I can verify the completeness and immutability of audit logs

  Background:
    Given a user with rejection permissions is logged into the system
    And the audit trail service and database are operational
    And at least one rejection action has been performed and logged

  Scenario: Verify audit trail API returns complete rejection logs
    When the user performs a rejection action on a request
    And the user retrieves the audit trail via the API filtered by rejection actions
    Then the audit trail API returns all rejection actions with timestamps, user details, and action types

  Scenario: Verify audit logs are immutable
    Given the user has retrieved the audit trail rejection logs
    When the user attempts to modify a retrieved audit log
    Then the system rejects or ignores the modification
    And the audit log remains unchanged when retrieved again

  Scenario: Verify audit trail data is displayed correctly in the UI
    When the user views the audit trail UI filtered by rejection actions
    Then the audit trail view displays all rejection actions with correct details

  Scenario: Verify system performance with frequent logging
    When the user views the audit trail UI
    Then the audit trail page loads within acceptable performance limits
