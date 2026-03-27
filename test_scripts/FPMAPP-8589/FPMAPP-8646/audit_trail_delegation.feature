# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8646
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:42:32
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging for Delegation Action with Missing Previous Value
  
  As a compliance officer
  I want comprehensive audit trails for all approval-related actions
  So that I can ensure compliance and traceability even when previous values are missing

  Background:
    Given the user "compliance.officer" is authenticated and authorized to delegate approval actions
    And the system has the modified POST /fpm/staffing/approval API deployed
    And the Audit_Logs table schema is extended with required columns and indexes

  Scenario: Perform delegation action without previous_value and verify audit trail
    When the user performs a delegation action on staffing request "staffingReq789" to delegate to user "user456" without providing previous_value
    Then the API response should include audit trail references
    And the audit log entry for the delegation action should exist with:
      | action_type          | delegation          |
      | entity_type          | staffing            |
      | entity_id            | staffingReq789      |
      | performed_by_user_id | compliance.officer  |
      | previous_value       | <null>              |
      | new_value            | user456             |
      | immutable            | true                |
    And the audit log entry timestamp should be correctly recorded
    And the audit log entry should be immutable and secured
    And the audit data should remain queryable and indexed despite missing previous_value
