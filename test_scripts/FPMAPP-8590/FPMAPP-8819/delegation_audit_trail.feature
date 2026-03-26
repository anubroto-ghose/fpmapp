# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8819
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:50:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Audit Trail Logging
  As a compliance officer
  I want a full audit trail capturing every delegation action
  So that delegated user information is accurately recorded and immutable

  Background:
    Given the Approval_Audit_Trail table is available
    And a user with delegation rights is logged in
    And the system supports delegation of approval requests

  Scenario: Delegate an approval request and verify audit trail logging
    When the user delegates an approval request with ID "12345" to user "user-6789"
    Then the audit trail entry for the approval request "12345" should include:
      | action_type | delegation |
    And the audit trail entry should record delegated user information:
      | user_id  | user-6789 |
      | username | delegateUser |
    And the delegation timestamp should be correctly logged
    And the audit trail entry should be immutable
    And the delegation event can be filtered and retrieved via the API by request ID "12345" and action type "delegation"
