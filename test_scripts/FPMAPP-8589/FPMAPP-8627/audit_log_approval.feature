# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8627
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:55:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Entry Creation for Approval Actions
  As a compliance officer
  I want comprehensive audit trails for all approval actions
  So that I can ensure compliance and traceability of financial approvals

  Background:
    Given the Fpm service is deployed with extended audit logging framework
    And the database schema includes audit_logs columns: action_type, user_id, timestamp, details
    And a user with username "compliance_officer" and valid credentials is logged in and authorized to perform approval actions

  Scenario: Successful creation of audit log entry upon deal sheet approval
    When the user navigates to the deal sheet with ID 5001
    And the user performs an approval action on the deal sheet
    Then the system should call the POST /fpm/audit/log API with:
      | action_type | approval |
      | user_id     | 1001     |
      | timestamp   | current  |
      | details     | JSON with approval metadata including dealSheetId and approvedBy |
    And an audit log entry should be created in the audit_logs table with:
      | action_type | approval |
      | user_id     | 1001     |
      | details     | JSON containing approval metadata |
    And the audit log entry should be immutable and stored securely
    And the audit log entry should be indexed for efficient querying

  Scenario Outline: Audit log entry creation with different deal sheet IDs
    Given the user is logged in as "compliance_officer"
    When the user navigates to the deal sheet with ID <dealSheetId>
    And the user performs an approval action on the deal sheet
    Then the audit log entry should contain the dealSheetId <dealSheetId> in the details

    Examples:
      | dealSheetId |
      | 5001       |
      | 5002       |
      | 5010       |
