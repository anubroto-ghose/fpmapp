# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6226
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:36:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Delegation Enforcement and Audit Logging
  As an approver with delegation eligibility
  I want to delegate approval tasks only when permitted
  So that unauthorized delegations are blocked and all delegation actions are fully audited

  Background:
    Given the following users exist with roles and delegation eligibility:
      | UserId | Role                 | CanDelegate |
      | userD  | Approver_Manager     | true        |
      | userE  | Approver_Analyst     | false       |
      | userF  | Approver_SeniorAnalyst | false     |
      | userG  | Approver_JuniorAnalyst | false     |
    And User "userD" is logged in
    And an approval task with ID "approval123" exists and is assigned to User "userD"

  Scenario: Successful delegation from eligible user is processed and logged
    When User "userD" delegates approval task "approval123" to User "userF" for duration 3600 seconds
    Then the delegation action is accepted
    And the system logs the delegation with acting user "userD", delegatee "userF", and a timestamp
    And the delegatee "userF" has the approval task "approval123" assigned

  Scenario: Delegation attempt from a user without delegation rights is blocked
    Given User "userE" is logged in
    When User "userE" attempts to delegate approval task "approval123" to User "userG" for duration 3600 seconds
    Then the delegation action is rejected
    And an error message "User role not eligible for delegation" is returned
    And no delegation entry is created in the audit log for User "userE"
    And User "userG" does not receive the approval task "approval123"
