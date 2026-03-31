# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8919
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:10:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Audit Trail Logging
  As a compliance officer
  I want a complete and immutable audit trail of all approval decisions
  So that I can ensure compliance and traceability of financial approvals

  Background:
    Given a user with approval permissions is logged into the system
    And a request with ID "REQ-20240601-001" is submitted and pending approval

  Scenario: Approve a pending request and verify audit log entry
    When the user approves the pending request with ID "REQ-20240601-001"
    Then the audit log for request "REQ-20240601-001" should contain an entry with:
      | userId          | approver123          |
      | decisionDetails | Approved request     |
    And the audit log entry timestamp should be recorded accurately
    And the audit log entry should be immutable

  Scenario Outline: Verify audit log immutability
    Given the audit log entry for request "<requestId>" exists
    When an attempt is made to modify the audit log entry
    Then the modification should be rejected
    And the audit log entry remains unchanged

    Examples:
      | requestId          |
      | REQ-20240601-001   |
