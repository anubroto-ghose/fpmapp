# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8618
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:01:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow Thresholds
  As a financial approver
  I want hierarchical role-based approval workflows with thresholds
  So that approval requests are enforced according to role limits

  Background:
    Given the approval workflow system is deployed with role thresholds configured
    And test users exist with roles Director, Manager, and Employee

  Scenario: Approval request below manager threshold assigned to Manager
    When a Manager creates an approval request with amount 5000.00
    Then the request status should be "PENDING_MANAGER_APPROVAL"
    When the Manager approves the request
    Then the request status should be "APPROVED"

  Scenario: Approval request above manager threshold but below director threshold assigned to Manager
    When a Manager creates an approval request with amount 20000.00
    Then the request status should be "ESCALATED_TO_DIRECTOR"
    When a Director approves the escalated request
    Then the request status should be "APPROVED"

  Scenario: Approval request above director threshold assigned to Director
    When a Director creates an approval request with amount 60000.00
    Then the request status should be "PENDING_DIRECTOR_APPROVAL"
    When the Director approves the request
    Then the request status should be "APPROVED"

  Scenario: Unauthorized approval attempt by Employee
    When an Employee creates an approval request with amount 3000.00
    Then the request status should be "REJECTED_UNAUTHORIZED"

