# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8944
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:46:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Creation for Delegation Actions
  As a compliance officer
  I want a full audit trail of all approval, rejection, and delegation actions
  So that I can ensure compliance and traceability

  Background:
    Given the user "testuser" is authenticated and authorized to delegate approval actions
    And the delegation feature and audit logging system are operational

  Scenario: Delegate an approval request and verify audit log creation including override
    Given an approval request with ID "approvalReq789" exists
    When the user delegates the approval request to user "user456" with comment "Delegating approval due to workload."
    And the user performs an override delegation action with comment "Override due to urgent compliance requirement."
    And the user submits the delegation
    Then audit log entries are created capturing delegation and override actions with user IDs, timestamps, and action types
    And comments or reasons for delegation and override are logged if provided
    And audit logs remain immutable and securely stored
    And audit trail data is accessible via UI and API for the approval request

  # Additional scenarios could be added here for rejection and approval audit trails

