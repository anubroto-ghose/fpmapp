# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8628
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:54:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Delegation Action with Invalid User ID
  As a compliance officer
  I want the system to reject audit log entries for delegation actions with invalid user IDs
  So that audit logs remain accurate and untampered

  Background:
    Given the Fpm service audit logging framework is active
    And the audit_logs table schema is updated
    And the system is ready to accept delegation actions

  Scenario: Attempt to create audit log entry with invalid user_id
    When I attempt to perform a delegation action with user_id "invalid-user-123"
    And I submit the audit log entry with action_type "delegation" and details "Delegation attempted with invalid user id"
    Then the system should reject the audit log entry creation
    And no audit log entry should be created with user_id "invalid-user-123"
    And the system returns an error response indicating invalid user metadata
    And the audit_logs table remains untampered
