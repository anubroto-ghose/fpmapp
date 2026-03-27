# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8892
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:29:13
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Prevent unauthorized approval outside role scope
  
  As an approver
  I want the system to route approval requests based on hierarchical roles
  So that unauthorized users cannot approve requests outside their scope

  Background:
    Given the role hierarchy and approval scopes are configured
    And an approval request with ID 1001 is routed to the Manager role

  Scenario: Unauthorized user attempts to approve a Manager-level request
    Given I am logged in as a user with role "Staff" who is not authorized to approve this request
    When I attempt to approve the approval request with ID 1001
    Then I should see an error message "Access Denied: You are not authorized to approve this request."
    And the approval status for request 1001 should remain "Pending"
    And the requester should not see any unauthorized approval status update
