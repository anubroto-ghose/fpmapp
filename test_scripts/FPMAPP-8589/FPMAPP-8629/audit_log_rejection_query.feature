# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8629
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:54:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Rejection Query
  As a compliance officer
  I want to query audit logs filtered by rejection actions, user_id, and timestamp range
  So that I can review all rejection audit trail entries efficiently and accurately

  Background:
    Given the audit logs contain multiple entries including rejection actions
    And indexes exist on user_id and timestamp columns
    And the reporting tool is available

  Scenario: Query rejection audit logs filtered by user_id and timestamp range
    Given I am on the audit log query page
    When I filter audit logs by action_type "rejection"
      And I filter audit logs by user_id "user123"
      And I filter audit logs by timestamp range from "2024-06-01T00:00" to "2024-06-02T23:59"
      And I submit the query
    Then I should see only rejection audit log entries matching user_id "user123" and the timestamp range
    And each entry should include user_id, timestamp, action_type, and details
    And the query should execute efficiently
    And the audit log data integrity and immutability should be maintained
