# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8951
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:39:46
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based Approval Workflow
  As an approver
  I want the system to enforce hierarchical role-based approval workflows
  So that approval status and current approver role are updated and persisted correctly

  Background:
    Given the approval requests and user roles tables support role hierarchy and approval status
    And an approval request with ID 100 is pending approval
    And a user with role "ROLE_APPROVER_LEVEL_1" exists and is authorized to approve

  Scenario: Approve a pending request and verify status and approver role update
    When the authorized approver approves the request with ID 100
    Then the approval status in the database should be "Approved"
    And the current approver role in the database should be updated accordingly
    And the approval workflow API response for request ID 100 should reflect the updated approval status and current approver role
    And data consistency is maintained between the database and API responses
