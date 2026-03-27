# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8634
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:50:29
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation rules restrict delegation to authorized roles only
  
  As a financial approver
  I want hierarchical role-based approval workflows with delegation restricted to authorized roles
  So that approval tasks are delegated only to valid roles and improper delegation is prevented

  Background:
    Given delegation rules are configured in the system
    And only authorized roles are allowed to delegate approval tasks
    And delegation info is available and can be passed in workflow initiation

  Scenario: Delegation from an authorized role succeeds and routes tasks correctly
    Given I am logged in as a user with role "FIN_APPROVER"
    When I attempt to delegate an approval task to role "FIN_APPROVER"
    Then the delegation should succeed
    And the task should be routed correctly to the delegated role

  Scenario: Delegation attempt from an unauthorized role is rejected
    Given I am logged in as a user with role "STAFF"
    When I attempt to delegate an approval task to role "FIN_APPROVER"
    Then the delegation should be rejected with an error message "Delegation not allowed"

  Scenario Outline: Workflow initiation respects delegation_info and enforces delegation rules
    When I initiate a workflow with delegation_info role "<role>" and delegator "<delegator>"
    Then the workflow initiation should <result>

    Examples:
      | role         | delegator           | result   |
      | FIN_APPROVER | authorized_approver | succeed  |
      | STAFF        | unauthorized_user   | fail     |

  Scenario Outline: Approve tasks with delegation_flag set to true and false
    Given a task with id <taskId> exists
    When I approve the task with delegation_flag set to <delegationFlag>
    Then the approval status should be "APPROVED"
    And the delegation flag should be <delegationFlag>

    Examples:
      | taskId | delegationFlag |
      | 1001   | true          |
      | 1001   | false         |
