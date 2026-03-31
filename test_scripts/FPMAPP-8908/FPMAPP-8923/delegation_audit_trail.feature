# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8923
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:06:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Action Audit Trail
  As a compliance officer
  I want a complete and immutable audit trail of all delegation actions
  So that I can verify accountability and compliance

  Background:
    Given a user with delegation rights "delegatorUser" is logged in
    And a request with ID "REQ-12345" is assigned to the user

  Scenario: Delegate approval responsibility and verify audit log
    When the user delegates the approval responsibility for request "REQ-12345" to user "delegateeUser" with reason "Delegation for workload balancing"
    Then the audit log records the delegation action
    And the audit log entry includes the delegator user "delegatorUser"
    And the audit log entry includes the delegatee user "delegateeUser"
    And the audit log entry includes a timestamp
    And the audit log entry includes the reason "Delegation for workload balancing"
    And the delegation entry is immutable
    And the audit log can be queried by delegation action and users involved
