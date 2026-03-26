# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8820
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:51:46
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Retrieval API
  As a compliance officer
  I want to retrieve a full audit trail for approval requests
  So that I can verify all approval, rejection, delegation, and override actions

  Background:
    Given multiple audit trail entries exist for various approval requests and action types
    And the audit trail retrieval API is accessible at "/api/approvals/audit-trail/{requestId}"

  Scenario: Retrieve all audit trail entries for a valid request ID without action type filter
    When I call the audit trail retrieval API with request ID "REQ12345" and no action type filter
    Then the API returns all audit trail entries for request ID "REQ12345"
    And the response includes entries with action types "approval", "delegation", "override"
    And each entry contains action_type, timestamp, user details, delegation info, and comments

  Scenario: Retrieve only approval actions for a valid request ID with action type filter
    When I call the audit trail retrieval API with request ID "REQ12345" and action type filter "approval"
    Then the API returns only audit trail entries with action type "approval" for request ID "REQ12345"
    And each entry contains action_type "approval" and all required fields

  Scenario: Retrieve audit trail entries for an invalid or non-existent request ID
    When I call the audit trail retrieval API with request ID "INVALID_REQ"
    Then the API returns an empty list or appropriate error message

  # Step Definitions would be implemented in Java to mock service responses and verify API outputs
