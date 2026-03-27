# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8818
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:50:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging for Approval Actions
  As a compliance officer
  I want a full audit trail capturing every approval, rejection, delegation, and override action
  So that I can ensure compliance and traceability of financial approvals

  Background:
    Given the Approval_Audit_Trail table exists and is accessible
    And the system is configured to log audit trail entries
    And a user "compliance_officer" with ID "user123" is authenticated and authorized

  Scenario: Verify audit trail logs approval action with all required details
    When the user performs an approval action on a valid approval request with comment "Approved after review - all checks passed."
    Then a new audit trail record is created with action_type "approval"
    And the timestamp reflects the exact time of the action
    And the user details with ID "user123" and username "compliance_officer" are correctly recorded
    And the comments "Approved after review - all checks passed." are stored
    And the audit trail entry is immutable and cannot be altered after creation
