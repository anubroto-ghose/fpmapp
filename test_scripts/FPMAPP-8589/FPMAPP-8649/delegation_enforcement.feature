# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8649
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:40:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Enforcement for Role-Based Approval Workflow
  
  As an approver
  I want hierarchical role-based approval workflows with delegation enforcement
  So that only authorized roles can delegate approval requests

  Background:
    Given delegation rules are configured specifying authorized roles
    And approval workflows support delegation parameters

  Scenario: Delegation allowed for authorized role (manager)
    Given I am logged in as a user with role "manager"
    And I have an approval request with ID "12345"
    When I attempt to delegate the approval request to user "user_delegatee"
    Then the delegation should be accepted
    And the delegation information including delegated_to_user_id and delegation_timestamp should be recorded
    And the system audit logs should capture the delegation attempt with user details and timestamp

  Scenario: Delegation rejected for unauthorized role (employee)
    Given I am logged in as a user with role "employee"
    And I have an approval request with ID "12345"
    When I attempt to delegate the approval request to user "user_delegatee"
    Then the delegation should be rejected
    And an appropriate error message "You are not authorized to delegate approvals" should be displayed
    And the delegation information should not be recorded
    And the system audit logs should capture the delegation attempt with user details and timestamp
