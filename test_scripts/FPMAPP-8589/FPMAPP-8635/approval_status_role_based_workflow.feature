# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8635
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:49:46
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval status updates reflect role-based decisions and delegation states
  
  Background:
    Given the approval workflow system is integrated with the Fpmcamunda service
    And approval tasks exist with various roles and delegation states

  Scenario: Approve a task as a direct approver without delegation
    Given I am logged in as a direct approver
    When I approve the task with ID "task-001" without delegation
    Then the approval status for task "task-001" should be "APPROVED"
    And the delegation flag for task "task-001" should be false
    And the next routing information should be accurate based on role and delegation

  Scenario: Approve a task as a delegated approver with delegation_flag true
    Given I am logged in as a delegated approver
    When I approve the task with ID "task-002" with delegation
    Then the approval status for task "task-002" should be "APPROVED"
    And the delegation flag for task "task-002" should be true
    And the next routing information should be accurate based on role and delegation

  Scenario: Reject a task as a delegated approver
    Given I am logged in as a delegated approver
    When I reject the task with ID "task-002" with delegation
    Then the approval status for task "task-002" should be "REJECTED"
    And the delegation flag for task "task-002" should be true

  Scenario: Handle edge case where delegation_flag is inconsistent with role
    Given a task with ID "task-003" exists with role "FINANCIAL_APPROVER" and delegation_flag true
    When I attempt to approve the task with ID "task-003" with delegation
    Then the approval should fail due to inconsistent delegation flag
