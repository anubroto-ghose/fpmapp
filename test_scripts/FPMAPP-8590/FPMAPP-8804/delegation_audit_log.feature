# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8804
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:39:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Audit Trail Logging
  As a compliance officer
  I want a full audit trail of all delegation actions
  So that I can verify delegator, delegatee, and delegation time are logged correctly

  Background:
    Given a user with delegation permissions is logged into the system
    And the AuditTrailService and audit log database tables are operational

  Scenario: Verify delegation action audit log includes delegator, delegatee, and delegation time
    When the user performs a delegation action assigning approval rights from "user123" to "user456" for approval request "approvalReq789"
    Then an audit log entry is created immediately after the delegation action
    And the audit log entry contains delegator user details "user123"
    And the audit log entry contains delegatee user details "user456"
    And the audit log entry contains the exact delegation timestamp
    And the audit log entry is immutable and stored securely
    And the audit trail can be viewed correctly in the UI audit trail view for approval request "approvalReq789"
