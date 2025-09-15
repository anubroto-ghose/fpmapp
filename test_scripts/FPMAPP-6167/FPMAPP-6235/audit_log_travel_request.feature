# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6235
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:29:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Travel Request Audit Log
  As an authorized approver
  I want to retrieve and verify the audit log for a travel request
  So that I can ensure the approval actions were tracked correctly

  Background:
    Given a travel request with ID "TRV-20250915-0001" exists
    And multiple approval actions have been performed on the travel request
    And audit logs exist for these actions with users, timestamps, and statuses

  Scenario: Query audit logs filtered by travel request ID and verify completeness and accuracy
    When I query the audit logs for travel request "TRV-20250915-0001"
    Then the audit log should contain 3 records
    And each audit record contains:
      | userId         | actionType | previousStatus | newStatus  | remarks                         |
      | user-approver1 | APPROVE    | PENDING        | APPROVED   | Approved by level 1 approver    |
      | user-delegator | DELEGATE   | APPROVED       | DELEGATED  | Delegated approval to user-approver2 |
      | user-approver2 | REJECT     | DELEGATED      | REJECTED   | Rejected due to missing documents |
    And the audit records are sorted by actionTimestamp in ascending order

  Scenario Outline: Validate each audit record timestamps and user IDs
    Given the audit logs for travel request "<travelRequestId>" are retrieved
    Then each audit record should have a valid timestamp and user ID

    Examples:
      | travelRequestId         |
      | TRV-20250915-0001      |
