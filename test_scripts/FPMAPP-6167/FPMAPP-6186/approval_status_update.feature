# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6186
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:07:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval status updates and audit trail logging
  
  As an approver
  I want hierarchical role-based approval workflows
  So that approval actions reflect accurate statuses with user IDs and timestamps, and audit trails are maintained

  Background:
    Given the approval workflows are implemented and active
    And the audit trail system is integrated with approval controllers

  @dealsheet
  Scenario: Approve a deal sheet and verify status update and audit log
    Given I am logged in as an approver with user ID "user123"
    And I navigate to the deal sheet approval page for deal sheet ID "deal-abc-123"
    When I approve the deal sheet
    Then the approval status should be updated to "Approved"
    And the status update should record the user ID "user123" and a timestamp
    And the audit trail for deal sheet "deal-abc-123" should contain an entry for approval by "user123"

  @staffing
  Scenario: Reject a staffing request and verify status update and audit log
    Given I am logged in as an approver with user ID "user456"
    And I navigate to the staffing request approval page for request ID "staff-xyz-789"
    When I reject the staffing request
    Then the approval status should be updated to "Rejected"
    And the status update should record the user ID "user456" and a timestamp
    And the audit trail for staffing request "staff-xyz-789" should contain an entry for rejection by "user456"

  @travel
  Scenario: Delegate a travel request approval and verify status change and audit log
    Given I am logged in as an approver with user ID "user789"
    And I navigate to the travel request delegation page for travel request ID "travel-555-aaa"
    When I delegate the approval to user ID "user999"
    Then the delegation status should be updated to "Delegated"
    And the delegation action should record the delegator user ID "user789" and a timestamp
    And the audit trail for travel request "travel-555-aaa" should contain an entry for delegation by "user789" to "user999"

  @audit
  Scenario: Verify audit trail contains all approval-related events in real-time
    Given approval ID "approve-111-222" has approval, rejection, and delegation events
    When I retrieve the audit trail for approval ID "approve-111-222"
    Then the audit trail should contain entries for approval, rejection, and delegation actions
    And each entry should have accurate user IDs and timestamps
