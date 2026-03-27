# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8808
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:56:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Unauthorized Role Cannot Approve or Delegate Requests
  
  As a user with a role not authorized to approve or delegate
  I want to be prevented from approving or delegating requests
  So that the system enforces role-based access control on approval workflows

  Background:
    Given a user "unauthorizedUser" with role "ROLE_EMPLOYEE" is logged in
    And a request with ID "REQ12345" is pending approval

  Scenario: Unauthorized user attempts to approve a pending request
    When the user attempts to approve the request "REQ12345"
    Then the system prevents the approval
    And an "Access Denied" error message is displayed
    And the request status remains "PENDING"

  Scenario: Unauthorized user attempts to delegate a pending request
    When the user attempts to delegate the request "REQ12345" to "managerUser"
    Then the system prevents the delegation
    And an "Access Denied" error message is displayed
    And the request status remains "PENDING"
