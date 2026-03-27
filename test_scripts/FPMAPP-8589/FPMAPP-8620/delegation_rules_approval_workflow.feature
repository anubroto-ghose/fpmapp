# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8620
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:00:20
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Rules Restrict Delegation to Authorized Roles Only
  
  As a financial approver
  I want hierarchical role-based approval workflows with delegation restrictions
  So that delegation is allowed only to authorized roles

  Background:
    Given the delegation rules are configured in the approval_workflow database
    And the approval workflow API supports delegation_info in the request

  Scenario: Delegate approval request from Manager to authorized delegate (Manager)
    When the Manager attempts to delegate an approval request to another Manager
    Then the delegation succeeds
    And the approval request is routed to the authorized delegate

  Scenario: Delegate approval request from Manager to unauthorized delegate (Employee)
    When the Manager attempts to delegate an approval request to an Employee
    Then the delegation is rejected with an unauthorized delegation error

  Scenario Outline: Delegate approval request from Director to delegate
    When the Director attempts to delegate an approval request to a <delegateRole>
    Then the delegation <result>

    Examples:
      | delegateRole | result    |
      | Manager      | succeeds  |
      | Employee     | is rejected with an unauthorized delegation error |

