# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8818
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:50:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging for Approval Actions
  As a compliance officer
  I want the system to capture a full audit trail for every approval action
  So that I can verify compliance and traceability of approval workflows

  Background:
    Given the Approval_Audit_Trail table exists and is accessible
    And the system is configured to log audit trail entries
    And a user "compliance.officer" with ID "user123" is authenticated and authorized
    And an approval request with ID "approvalReq-456" exists

  Scenario: Approval action creates a correct audit trail entry
    When the user performs an approval action on the approval request "approvalReq-456" with comment "Approved after compliance review."
    Then a new audit trail record is created with:
      | action_type | approval |
      | approval_request_id | approvalReq-456 |
      | performed_by_user_id | user123 |
      | comments | Approved after compliance review. |
    And the audit trail entry timestamp reflects the exact time of the action
    And the audit trail entry is immutable and cannot be altered after creation
