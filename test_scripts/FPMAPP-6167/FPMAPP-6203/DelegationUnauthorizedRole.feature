# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6203
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:53:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation authorization enforcement
  As a user without delegation rights
  I want attempts to delegate approval to be denied
  So that the system enforces role-based delegation restrictions and audit logs the denials

  Background:
    Given the system has an active approval request with ID 12345 pending user action
    And I am logged in as user "user1001" with role "UnauthorizedRole"

  @HighPriority
  Scenario: Unauthorized user attempts to delegate approval and is rejected
    When I attempt to delegate approval request "12345" to role "AuthorizedRole"
    Then the delegation request is rejected with an error message containing "delegation denied"
    And the approval status and current approver role remain unchanged
    And an audit log entry is created recording the delegation attempt with denial reason
