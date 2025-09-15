# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6216
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:42:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Hierarchical role-based approval workflows
  As an approver
  I want approval requests automatically routed to the correct approvers
  So that I see only the requests relevant to my role and receive notifications

  Background:
    Given the system has configured role mappings and workflows
    And active users with roles "Manager" and "Director" exist

  @dealSheet
  Scenario: Approval request routing for deal sheet
    When a user submits a deal sheet approval request with amount 50000
    Then the request should be assigned to an approver with role "Manager"
    And the assigned approver should receive a notification
    When the assigned approver logs in
    Then the approval request should be visible in their approval list

  @staffing
  Scenario: Approval request routing for staffing over threshold
    When a user submits a staffing approval request with amount 120000
    Then the request should be assigned to an approver with role "Director"
    And the assigned approver should receive a notification
    When the assigned approver logs in
    Then the approval request should be visible in their approval list

  @travel
  Scenario: Approval request routing for travel request
    Given the user requests approval routed to role "Director"
    When the user submits a travel approval request
    Then the request should be assigned to an approver with role "Director"
    And the assigned approver should receive a notification
    When the assigned approver logs in
    Then the approval request should be visible in their approval list
