# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8808
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:43:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Unauthorized Role Approval and Delegation Prevention
  As a user with an unauthorized role
  I want to be prevented from approving or delegating approval requests
  So that the system enforces role-based access control on approvals

  Background:
    Given a user is logged in with role "employee"  # unauthorized role
    And there is a pending approval request with ID "REQ12345"

  Scenario: Unauthorized user attempts to approve a pending request
    When the user attempts to approve the request with ID "REQ12345"
    Then the system prevents the approval action
    And an access denied error message is displayed
    And the request status remains "pending"

  Scenario: Unauthorized user attempts to delegate a pending request
    When the user attempts to delegate the request with ID "REQ12345"
    Then the system prevents the delegation action
    And an access denied error message is displayed
    And the request status remains "pending"
