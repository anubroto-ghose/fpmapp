# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8619
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:01:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow Routing
  As a financial approver
  I want hierarchical role-based approval workflows with thresholds
  So that approval requests are routed correctly based on role hierarchy

  Background:
    Given the role hierarchy is defined as Employee < Manager < Director
    And approval thresholds are set as:
      | Role     | Threshold |
      | Employee | 0         |
      | Manager  | 10000     |
      | Director | 50000     |

  Scenario: Employee submits approval request requiring Manager approval
    Given an Employee submits an approval request with amount 8000
    When the approval request is processed
    Then the request should be routed to the Manager automatically

  Scenario: Manager submits approval request requiring Director approval
    Given a Manager submits an approval request with amount 20000
    When the approval request is processed
    Then the request should be routed to the Director automatically

  Scenario: Director submits approval request within their approval threshold
    Given a Director submits an approval request with amount 30000
    When the approval request is processed
    Then the request should be routed to the Director for approval
