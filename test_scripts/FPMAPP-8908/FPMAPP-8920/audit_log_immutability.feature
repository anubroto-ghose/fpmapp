# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8920
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:09:17
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Immutability After Approval
  As an approver user
  I want to ensure that audit log entries cannot be modified after an approval decision
  So that the audit trail remains immutable and trustworthy

  Background:
    Given a user with approval permissions is logged into the system
    And a request with ID "REQ-20240601-001" is submitted and pending approval

  Scenario: Approve a pending request and verify audit log immutability
    When the user approves the pending request with ID "REQ-20240601-001"
    And the user accesses the audit log entry for the approved request with ID "AUDIT-REQ-20240601-001"
    And the user attempts to modify the audit log entry's user ID, timestamp, and decision details
    Then the system prevents any modification to the audit log entry
    And the audit log entry remains unchanged and immutable
    And any attempt to alter the audit log is logged or rejected with an error message
