# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6234
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:30:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation allowed only for permitted roles and properly audited
  As an authorized approver
  I want a hierarchical, role-based approval workflow with audit trails
  So that only users with permitted roles can delegate approval and all delegations are audited

  Background:
    Given a staffing request with id 1001 exists and is pending approval
    And delegation is allowed only for role "Director"

  Scenario: Successful delegation by Director role to Manager role
    Given the user "directorUser" has role "Director"
    When the user sends a POST request to "/staffing/request/approval" with:
      | action           | delegate |
      | roleId           | Director |
      | delegatedToRoleId | Manager |
    Then the response status is 200
    And the delegation is successful
    And the staffing request current approver role is "Manager"
    And the delegation details are recorded in the database
    And the approval audit log records the delegation action with user id "directorUser"
    And the response includes updated approvalStatus, currentApproverRole, and auditLogId

  Scenario: Delegation rejected for role not permitted to delegate
    Given the user "managerUser" has role "Manager"
    When the user sends a POST request to "/staffing/request/approval" with:
      | action           | delegate |
      | roleId           | Manager |
      | delegatedToRoleId | TeamLead |
    Then the response status is 403
    And the response contains message "Delegation not permitted for role"
    And no delegation record is created
    And no audit log record is created
