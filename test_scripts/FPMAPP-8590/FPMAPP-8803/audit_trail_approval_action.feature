# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8803
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:00:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging for Approval Actions
  As a compliance officer
  I want a full audit trail of all approval actions
  So that I can verify that approvals are logged correctly and immutably

  Background:
    Given a user with approval permissions is logged into the system
    And the AuditTrailService and audit log database tables are operational

  Scenario: Verify audit log creation for approval action with correct details
    When the user performs an approval action on a request
    Then an audit log entry is created immediately after the approval action
    And the audit log entry contains a timestamp
    And the audit log entry contains the user details of the approver
    And the audit log entry contains the action type "approval"
    And the audit log entry is immutable and stored securely
    And the audit trail can be viewed correctly in the UI audit trail view
