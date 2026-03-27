# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8891
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:29:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Routing Based on Role Hierarchy
  
  As an approver
  I want the system to route approval requests based on hierarchical roles
  So that mid-tier requests are routed to Managers correctly

  Background:
    Given the role hierarchy is configured with "Manager" below "Director"
    And a requestor "requestorUser" is authorized to submit requests
    And the system supports mid-tier approval requests between 5000 and 20000

  Scenario: Submit a mid-tier approval request and verify routing to Manager
    When the requestor submits an approval request with amount 15000 and description "Mid-tier approval request for project X"
    Then the request should be routed automatically to a user with role "Manager"
    And the Manager should receive an email notification with subject "New Approval Request"
    And the Manager should receive an in-app notification with message "New approval request assigned to you."
    And no other roles outside "Manager" should receive the request
    And the requestor should see the approval status updated to "Pending Manager Approval" in real-time
