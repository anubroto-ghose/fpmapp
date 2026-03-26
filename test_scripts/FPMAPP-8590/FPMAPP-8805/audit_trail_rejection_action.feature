# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8805
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:40:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Rejection Action Logging and Immutability
  As a compliance officer
  I want a full audit trail of all rejection actions
  So that I can verify compliance and ensure audit logs are immutable

  Background:
    Given a user with rejection permissions is logged into the system
    And the AuditTrailService and database tables for audit logs are operational
    And at least one rejection action has been performed and logged for request ID 12345

  Scenario: Retrieve audit trail and verify rejection action is present
    When the user retrieves the audit trail for request ID 12345 via the API
    Then the audit trail contains at least one rejection action
    And each rejection action includes timestamps, user details, and action types

  Scenario: Attempt to modify an audit log entry and verify immutability
    Given the user has retrieved the audit trail for request ID 12345
    When the user attempts to modify a rejection audit log entry
    Then the system rejects or ignores the modification
    And the audit log entry remains unchanged

  Scenario: Verify audit trail data is displayed correctly in the UI
    When the user views the audit trail for request ID 12345 in the UI
    Then the UI displays all rejection actions with correct details

  Scenario: Performance remains acceptable despite frequent logging
    When the user retrieves the audit trail for request ID 12345 multiple times
    Then each retrieval completes within 2 seconds
