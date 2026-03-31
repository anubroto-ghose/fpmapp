# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8969
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:34:40
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Status Update
  As an authorized approver
  I want to approve a pending approval request
  So that the approval status and current approver role are updated and persisted correctly

  Background:
    Given the approval requests and user roles tables are updated to support role hierarchy and approval status
    And an approval request with ID 1001 is pending approval
    And a user "approverUser" with role "Manager" is available and authorized to approve

  Scenario: Approve the request and verify status and approver role update
    When the user "approverUser" approves the approval request with ID 1001
    Then the approval status in the database for request ID 1001 should be "Approved"
    And the current approver role in the database for request ID 1001 should be "Director"
    And the approval workflow API response for request ID 1001 should reflect status "Approved" and current approver role "Director"
    And data consistency is maintained between the database and API responses
