# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8804
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:59:32
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Audit Log
  As a compliance officer
  I want a full audit trail of all delegation actions
  So that I can verify delegator, delegatee, and delegation time are logged correctly

  Background:
    Given a user with delegation permissions is logged into the system
    And the audit trail service and database are operational

  Scenario: Verify delegation action audit log includes delegator, delegatee, and delegation time
    When the user delegates approval rights from "delegatorUser" to "delegateeUser"
    Then an audit log entry is created immediately after the delegation action
    And the audit log entry contains delegator user details "delegatorUser"
    And the audit log entry contains delegatee user details "delegateeUser"
    And the audit log entry contains the exact delegation timestamp
    And the audit log entry is immutable and stored securely
    And the audit trail can be viewed correctly in the UI audit trail view
