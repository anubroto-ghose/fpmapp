# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9057
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:43:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation denied for unauthorized roles
  As a system administrator
  I want delegation capabilities restricted to specific roles
  So that unauthorized users cannot perform delegation actions

  Background:
    Given the delegation rules configuration includes authorized roles
    And the Camunda workflow engine is running and integrated with delegation rules
    And audit logging is enabled and accessible

  Scenario: Delegation attempt denied for user without authorized role
    Given I am authenticated as a user without an authorized role
    When I attempt to perform a delegation action with delegator_id 1001, delegatee_id 2002, and request_id 3003
    Then the delegation action is denied
    And the response indicates failure with reason "User role unauthorized for delegation"
    And no delegation action is logged in delegation_logs for this attempt
    And the audit logs contain the unauthorized delegation attempt for review
