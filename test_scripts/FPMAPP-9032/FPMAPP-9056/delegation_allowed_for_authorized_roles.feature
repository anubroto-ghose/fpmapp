# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9056
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:44:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation allowed for authorized roles
  As a system administrator
  I want delegation capabilities restricted to specific roles
  So that all delegation actions are logged and only authorized users can perform delegation

  Background:
    Given the delegation rules configuration includes authorized roles
    And the Camunda workflow engine is running and integrated with delegation rules
    And audit logging is enabled and accessible

  Scenario: Authorized user performs delegation successfully
    Given I am authenticated as a user with an authorized role
    When I perform a delegation action with delegator_id "user123", delegatee_id "user456", and request_id "req789"
    Then the response should indicate success
    And the delegation_logs table should contain a new entry with delegator_id "user123", delegatee_id "user456", and request_id "req789"
    And the audit logs should contain a delegation event for user "user123"
