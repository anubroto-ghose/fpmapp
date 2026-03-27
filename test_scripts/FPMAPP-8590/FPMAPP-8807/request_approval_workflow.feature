# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8807
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:57:19
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Request Approval Workflow - Rejection and Delegation
  
  As a director or manager with approval and delegation rights
  I want to reject or delegate financial requests
  So that the request status updates in real-time and the delegated approver receives the request

  Background:
    Given a user "directorUser" is logged in with roles "ROLE_DIRECTOR", "ROLE_APPROVER", "ROLE_DELEGATOR"
    And a request with ID "REQ12345" is pending approval

  Scenario: Reject a pending request and verify status update
    When the user rejects the request "REQ12345"
    Then the request status should update to "Rejected" immediately
    And the requester should see the status "Rejected" in real-time

  Scenario: Delegate a pending request to another approver
    Given a user "approver2" exists with role "ROLE_APPROVER"
    When the user delegates the request "REQ12345" to "approver2"
    Then the request status should update to "Delegated" immediately
    And the delegated approver "approver2" should receive the request
    And the requester should see the status "Delegated" in real-time

  Scenario: Unauthorized user cannot reject or delegate requests
    Given a user "unauthorizedUser" is logged in without approval or delegation roles
    When the user views the request "REQ12345"
    Then the user should not see options to reject or delegate the request
