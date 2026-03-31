# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8922
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:07:14
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Audit Trail Logging
  As a compliance officer
  I want a complete and immutable audit trail of all approval decisions
  So that I can ensure compliance and traceability of financial approvals

  Background:
    Given a user with approval permissions is logged into the system
    And a request with ID 1001 is submitted and pending approval

  Scenario: Approve a pending request and verify audit log entry
    When the user approves the pending request with ID 1001
    Then an audit log entry for the approval decision is created
    And the audit log entry includes the approving user's identity
    And the audit log entry includes the timestamp of the approval action
    And the audit log entry clearly logs the decision details as "APPROVED"
    And the audit log entry is immutable and cannot be altered
