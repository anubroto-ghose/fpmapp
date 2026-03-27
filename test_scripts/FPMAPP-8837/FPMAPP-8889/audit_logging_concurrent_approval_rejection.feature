# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8889
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:31:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Logging for Concurrent Approval and Rejection Actions
  As a compliance officer
  I want every approval or rejection action to be fully auditable with timestamps and user details
  So that audit logs are accurate, immutable, and system performance is maintained under concurrent load

  Background:
    Given the system is configured with audit logging enabled
    And multiple users have access to perform approval and rejection actions concurrently

  @performance @audit
  Scenario: Concurrent approval and rejection actions are logged correctly
    When multiple users perform approval and rejection actions simultaneously
    Then all approval and rejection actions should be logged with user ID, timestamp, and action type
    And audit logs should remain immutable and securely stored
    And system performance should not degrade beyond acceptable thresholds
    And no loss or corruption of audit log entries should occur under concurrent load

  Scenario Outline: Verify audit log entry for each user action
    Given user "<username>" is logged in
    When the user performs a "<action>" action on a pending approval item
    Then the audit log should contain an entry with user ID "<userId>", action type "<action>", and a valid timestamp
    And the audit log entry should be immutable

    Examples:
      | username | userId | action   |
      | user1    | 1      | APPROVAL |
      | user2    | 2      | REJECTION|
      | user3    | 3      | APPROVAL |
      | user4    | 4      | REJECTION|

