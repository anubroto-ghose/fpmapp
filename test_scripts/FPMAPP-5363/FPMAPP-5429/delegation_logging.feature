# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5429
# Epic: FPMAPP-5363
# Generated on: 2025-09-04 16:30:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation logging in approval workflows
  As a financial approver user
  I want to delegate an approval task to a designated user
  So that delegation actions are logged correctly and notifications sent

  Background:
    Given the user "financial.approver1" is logged in as a financial approver
    And the delegate user "delegate.user1" exists in the system

  Scenario: Delegate an approval task and verify delegation audit log and notifications
    Given the user navigates to the approval tasks list
    When the user delegates the task with id "<TASK_ID>" to the delegate user "delegate.user1"
    Then a delegation log entry shall be created with:
      | originalApprover    | delegatedToUser |
      | financial.approver1 | delegate.user1  |
    And the delegation log shall reference the task id "<TASK_ID>"
    And the delegation timestamp shall be recorded
    And notifications shall be sent to both the original approver and the delegate user

  Examples:
    | TASK_ID                            |
    | 00000000-0000-0000-0000-000000000001 |
