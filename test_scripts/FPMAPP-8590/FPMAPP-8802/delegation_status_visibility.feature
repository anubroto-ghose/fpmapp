# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8802
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:38:14
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Status Visibility
  As a manager who delegates approval rights
  I want both the delegator and delegatee to see accurate delegation status
  So that delegation changes are transparent and audit logged

  Background:
    Given a delegation record exists between "manager1" and "delegatee1"
    And both users "manager1" and "delegatee1" are logged in

  Scenario: Delegator views delegation status
    When the delegator "manager1" views the delegation status page
    Then the delegation status should be "ACTIVE" for the delegator
    And the audit logs should contain "Delegation created by manager1 to delegatee1"

  Scenario: Delegatee views delegation status
    When the delegatee "delegatee1" views the delegation status page
    Then the delegation status should be "ACTIVE" for the delegatee
    And the audit logs should contain "Delegation created by manager1 to delegatee1"

  Scenario: Delegator modifies delegation
    When the delegator "manager1" revokes the delegation to "delegatee1"
    And both users refresh their delegation status views
    Then the delegation status should be "REVOKED" for the delegator
    And the delegation status should be "REVOKED" for the delegatee
    And the audit logs should contain "Delegation modified by manager1 to status: REVOKED"
