# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8970
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:33:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Reject Approval Request
  As an authorized approver
  I want to reject an approval request
  So that the approval status and approver role are updated correctly and persisted

  Background:
    Given the approval requests and user roles tables are updated to support role hierarchy and approval status
    And an approval request with ID 1001 is pending approval
    And a user with the "FinanceManager" approver role is available

  Scenario: Reject the approval request and verify status and role updates
    When the authorized approver rejects the approval request with ID 1001
    Then the approval status in the database should be "Rejected"
    And the current approver role should be cleared or updated accordingly
    And the approval workflow API response should reflect the updated approval status and current approver role
    And data consistency should be maintained between the database and API responses
