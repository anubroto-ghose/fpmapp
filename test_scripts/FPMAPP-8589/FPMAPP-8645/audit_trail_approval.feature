# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8645
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:43:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging for Approval Actions
  
  As a compliance officer
  I want comprehensive audit trails for all approval-related actions
  So that I can ensure compliance and traceability of approvals

  Background:
    Given the user is authenticated as a compliance officer
    And the system has the modified POST /fpm/dealsheets/approval API deployed
    And the Audit_Logs table schema is extended with required columns and indexes

  Scenario: Successful approval action creates audit trail entry
    When the user performs an approval action on a deal sheet with valid approval metadata
    Then the API response should include audit trail references
    And a new audit log entry should exist with:
      | action_type          | approval   |
      | entity_type          | dealsheet |
      | entity_id            | DS-20240601-001 |
      | performed_by_user_id | compliance_officer_123 |
    And the audit log entry should have accurate timestamp
    And the previous_value and new_value fields should reflect the state change
    And the audit log entry should be immutable and secured with access controls
    And the audit data should be indexed for efficient querying

  Scenario: Approval action with invalid metadata returns error
    When the user performs an approval action on a deal sheet with invalid approval metadata
    Then the API response should return a 400 Bad Request error
    And no new audit log entry should be created
