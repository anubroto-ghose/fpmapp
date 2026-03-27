# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8821
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:48:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Routing Based on User Role and Financial Threshold
  
  As an approver
  I want role-based hierarchical approval workflows with financial thresholds
  So that approval requests are routed to the correct approver role based on amount

  Background:
    Given the following user roles and financial thresholds exist:
      | Role            | Threshold  |
      | JuniorApprover  | 10000      |
      | SeniorApprover  | 50000      |
      | ManagerApprover | 1000000    |
    And the approval workflow is configured with role-based routing and financial thresholds

  Scenario: Submit approval request below the first role's financial threshold
    When I submit an approval request with amount 5000
    Then the request should be routed to the "JuniorApprover" role

  Scenario: Submit approval request exceeding the first role's threshold but within the next role's threshold
    When I submit an approval request with amount 30000
    Then the request should be routed to the "SeniorApprover" role

  Scenario: Submit approval request exceeding all defined thresholds
    When I submit an approval request with amount 100000
    Then the request should be routed to the "ManagerApprover" role

  Scenario: Submit approval request with invalid negative amount
    When I submit an approval request with amount -100
    Then I should see an error message "Invalid amount"
