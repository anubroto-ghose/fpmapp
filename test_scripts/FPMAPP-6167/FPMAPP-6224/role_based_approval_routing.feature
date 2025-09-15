# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6224
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:37:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based hierarchical approval routing for deal sheets
  
  As an approver
  I want deal sheet approval requests routed automatically
  So that approvals respect role hierarchies and thresholds without manual intervention

  Background:
    Given the system has predefined role hierarchies and approval thresholds configured

  Scenario: Submit a deal sheet exceeding approver A's threshold routed to approver B
    Given a deal sheet approval request with amount $200,000 is submitted by user "submitter1"
    When the system processes the approval routing
    Then the approval request should be routed automatically to approver "approverB"
    And approver "approverA" should not receive the approval task
    And approver "approverB" should have the pending approval task for the deal sheet

  Scenario Outline: Approver sees only valid pending tasks based on role
    Given the deal sheet with amount <amount> is submitted by user "submitter1"
    When the system routes the approval
    Then approver "<approver>" should <visibility> the approval task

    Examples:
      | amount  | approver   | visibility |
      |  50000  | approverA  | see        |
      | 200000  | approverA  | not see    |
      | 200000  | approverB  | see        |
      | 600000  | approverB  | not see    |

  Scenario: Approver B logs in and views pending approvals
    Given user "approverB" is logged into the system
    When they navigate to the pending approvals page
    Then they should see the approval tasks assigned to their role

  Scenario: Approver A logs in and does not see approval tasks outside their threshold
    Given user "approverA" is logged into the system
    When they navigate to the pending approvals page
    Then they should not see approval tasks exceeding their approval threshold
