# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6204
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:52:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Audit Trail Logging
  
  As an authorized approver
  I want the system to log audit trails accurately for approve, reject, and delegate actions
  So that all approval status transitions can be traced with detailed metadata

  Background:
    Given an approval request with id "12345" exists in pending status
    And a user with id "1001" and role "ROLE_APPROVER_LEVEL_2" is logged in

  Scenario: Approve an approval request and verify audit logging
    When the user performs an "approve" action on the approval request with comments "Approving the deal sheet after review."
    Then the approval status should update to "Approved"
    And the audit trail should contain an entry with action "APPROVE", user id "1001", and the comment "Approving the deal sheet after review."
    And the API response should include a non-empty auditLogId

  Scenario: Reject an approval request and verify audit logging
    Given the approval request is reset to pending status
    When the user performs a "reject" action on the approval request with comments "Rejecting due to incomplete info."
    Then the approval status should update to "Rejected"
    And the audit trail should contain an entry with action "REJECT", user id "1001", and the comment "Rejecting due to incomplete info."
    And the API response should include a non-empty auditLogId

  Scenario: Delegate an approval request and verify delegation and audit logging
    Given the approver can delegate approvals to user with id "2002"
    When the user performs a "delegate" action on the approval request assigning to delegatee user id "2002" with delegation duration "60" minutes and comments "Delegating to junior approver."
    Then the approval status should update to "Delegated"
    And the delegation should be recorded with delegator user id "1001" and delegatee user id "2002"
    And the audit trail should contain an entry with action "DELEGATE", user id "1001", and the comment "Delegating to junior approver."
    And the API response should include a non-empty auditLogId
