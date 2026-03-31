# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8950
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:40:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow Enforcement
  As an approver
  I want the system to enforce hierarchical role-based approval workflows
  So that unauthorized users cannot approve requests beyond their role level

  Background:
    Given the following users exist:
      | username     | role     |
      | employeeUser | Employee |
      | managerUser  | Manager  |
      | directorUser | Director |
    And the following approval requests exist:
      | requestId | requiredRole |
      | 1001      | Manager      |
      | 2001      | Director     |

  Scenario: Employee attempts to approve a mid-tier request
    Given I am logged in as "employeeUser"
    When I navigate to approval request "1001"
    And I attempt to approve the request
    Then I should see an authorization error message

  Scenario: Manager attempts to approve a high-value request
    Given I am logged in as "managerUser"
    When I navigate to approval request "2001"
    And I attempt to approve the request
    Then I should see an authorization error message

  Scenario: Director approves a high-value request successfully
    Given I am logged in as "directorUser"
    When I navigate to approval request "2001"
    And I attempt to approve the request
    Then I should see a success message indicating approval
