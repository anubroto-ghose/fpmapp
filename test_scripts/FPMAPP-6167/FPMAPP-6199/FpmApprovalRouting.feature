# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6199
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:56:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Automatic routing of approval tasks
  
  As a requester or an approver
  I want approval requests to be automatically routed
  so that the correct approver receives the task based on hierarchical roles and thresholds

  Background:
    Given the approval workflow is configured with hierarchical roles and thresholds in Camunda
    And a requester user "requesterUser" exists
    And an approver user "approverUser" with the appropriate role exists

  Scenario: Submit approval request and verify task routing to correct approver
    When the requester submits a new approval request with amount 15000
    Then the approval task should be automatically routed to "approverUser"
    And the workflow state should reflect the task assignment

  Scenario Outline: Approval tasks are routed correctly based on amount thresholds
    Given the requester user "<requester>" exists
    And the approver user "<approver>" with role "<role>" exists
    When the requester submits a new approval request with amount <amount>
    Then the approval task should be automatically routed to "<approver>"
    And the workflow state should reflect the task assignment

    Examples:
      | requester    | approver     | role          | amount |
      | requesterUser| approverUser | Senior Manager| 15000  |
      | requesterUser| juniorApprov | Junior Manager| 5000   |

