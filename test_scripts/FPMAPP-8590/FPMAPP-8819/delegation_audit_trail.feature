# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8819
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:49:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Audit Trail Logging
  As a compliance officer
  I want a full audit trail capturing delegation actions
  So that delegated user information and timestamps are accurately recorded and immutable

  Background:
    Given the Approval_Audit_Trail table is available
    And a user with delegation rights "delegatorUser" is logged in
    And the system supports delegation of approval requests

  Scenario: Delegate an approval request and verify audit trail entry
    Given an approval request with ID "REQ123456" exists
    When the user delegates the approval request to user "delegateUser"
    Then the audit trail entry for the delegation action is recorded
    And the audit trail entry includes action_type set to "delegation"
    And the delegated user information with user ID and username is accurately recorded
    And the timestamp of delegation is correctly logged
    And the audit trail entry is immutable
    And the delegation event can be filtered and retrieved via the API by request ID "REQ123456" and action type "delegation"
