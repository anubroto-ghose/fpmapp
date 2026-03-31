# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8921
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:08:18
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Approval Decision
  As an approver user
  I want to verify audit log entries for approval decisions
  So that audit logs are correctly created or omitted based on request status

  Background:
    Given a user with approval permissions is logged into the system
    And a request with ID "REQ-20240601-001" is submitted and pending approval

  @PendingRequest
  Scenario: No audit log entry is created for pending requests
    When the user leaves the request pending without approval or rejection
    And the user accesses the audit log for the request
    Then no audit log entry should be created for the pending request

  @RejectedRequest
  Scenario: Audit log entry is created for rejected requests with correct details
    When the user rejects the request
    And the user accesses the audit log for the request
    Then an audit log entry should be created
    And the audit log entry should contain the user ID
    And the audit log entry should contain the timestamp of rejection
    And the audit log entry should indicate the decision as rejection
    And the audit log entry should be immutable
