# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6230
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:33:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Automatic routing of approval requests per hierarchical roles and thresholds
  
  As a requester or approver
  I want approval requests to be automatically routed
  So that they reach the correct approvers based on role hierarchy and threshold limits
  
  Background:
    Given approval workflows are configured properly in Camunda with defined hierarchical roles and thresholds
    And user accounts exist with assigned roles matching the workflow configuration
    And the system is integrated with Camunda and the FPM backend
  
  Scenario Outline: Submit approval request and verify correct automatic routing
    When a requester submits an approval request of amount <amount>
    Then the approval task should be assigned automatically to the role <expectedRole>
    And no manual task assignment is required
    And the routing follows the configured Camunda workflow rules
    And logs indicate successful task routing without errors
  
  Examples:
    | amount  | expectedRole   |
    | 5000    | ROLE_MANAGER   |
    | 20000   | ROLE_DIRECTOR  |
    | 100000  | ROLE_VP        |
