# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6193
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:00:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based hierarchical approval workflow
  As an approver
  I want approval requests to be automatically routed to the correct approver
  So that approval processes are streamlined and notifications are received promptly

  Background:
    Given the user "submitterUser" has role "Analyst" with approval limit of 10000
    And the approval hierarchy is configured
    And the deal sheet "Test deal sheet approval routing" requires approval amount $5000
    And the approver "approverUserId123" manages "Analyst" role

  Scenario: Approval request routing and notification
    When the user "submitterUser" submits the deal sheet for approval
    Then the approval request is routed to "approverUserId123"
    And the approver "approverUserId123" has the approval in their pending task list
    And the approver "approverUserId123" receives an in-app notification
    And the approver "approverUserId123" receives an email notification
