# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8807
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:42:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based approval workflow - rejection and delegation
  As a director or manager
  I want to reject or delegate deal sheets, staffing, and travel requests
  So that the request status updates in real-time and the delegated approver receives the request

  Background:
    Given a user is logged in as a "director" with approval and delegation rights
    And a request with ID "REQ12345" is pending approval

  @Rejection
  Scenario: Reject a pending request and verify status update
    When the user rejects the request with ID "REQ12345"
    Then the request status should update to "Rejected" immediately
    And the requester should see the status "Rejected" in real-time
    And only authorized roles can perform rejection

  @Delegation
  Scenario: Delegate a pending request to another authorized approver
    Given another user "manager_user2" is an authorized approver
    When the user delegates the request with ID "REQ12345" to "manager_user2"
    Then the request status should update to "Delegated" immediately
    And the delegated approver "manager_user2" should receive the request
    And the requester should see the status "Delegated" in real-time
    And only authorized roles can perform delegation

  @UnauthorizedAccess
  Scenario: Unauthorized user attempts to reject or delegate a request
    Given a user is logged in as a "regular_user" without approval rights
    When the user views the pending requests
    Then the user should not see options to reject or delegate any requests
