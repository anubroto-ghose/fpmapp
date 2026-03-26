# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8823
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:53:54
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status reflects detailed states and records timestamps and roles
  As an approver
  I want role-based hierarchical approval workflows with financial thresholds
  So that approval statuses update correctly and audit trails are maintained

  Background:
    Given the approval workflow system is active with status tracking enabled
    And there are approval requests submitted and pending

  Scenario: Approve an approval request and verify status changes to 'approved'
    When the approver with role "Manager" approves the approval request "req-12345"
    Then the approval status should be "approved"
    And the approval action should record the approver role "Manager"
    And the approval action should record a valid timestamp

  Scenario: Reject an approval request and verify status changes to 'rejected'
    When the approver with role "Manager" rejects the approval request "req-12345"
    Then the approval status should be "rejected"
    And the approval action should record the approver role "Manager"
    And the approval action should record a valid timestamp

  Scenario: Delegate an approval request and verify status changes to 'delegated'
    Given the approver with role "Manager" has delegation permission
    When the approver delegates the approval request "req-12345" to user "user-delegate-1"
    Then the approval status should be "delegated"
    And the delegation action should record the approver role "Manager"
    And the delegation action should record a valid timestamp

  Scenario: View the current approval status from a user perspective
    When the user views the approval request "req-12345"
    Then the current approval status should reflect the latest state
    And the audit trail should include all approval, rejection, and delegation actions with roles and timestamps
