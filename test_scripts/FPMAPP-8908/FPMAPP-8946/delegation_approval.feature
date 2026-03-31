# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8946
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:43:46
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation of Approval Authority
  As an approver
  I want to delegate my approval authority to another user with controlled permissions
  So that the delegate can approve requests within the delegated scope without unauthorized access

  Background:
    Given the approver "approverUser" is logged into the system
    And the delegate user "delegateUser" exists and is authorized to receive delegation
    And the approver has valid approval rights

  Scenario: Successful delegation of approval authority to an authorized user
    When the approver navigates to the delegation UI
    And selects the delegate user "delegateUser"
    And assigns approval rights with controlled permissions for project "PROJECT-1234"
    And confirms and submits the delegation request
    Then the delegation is accepted and saved
    And delegation flags are set on relevant approval requests
    And audit logs record the delegation action with timestamp and user details
    And the delegate user can approve requests within the delegated scope
    And no unauthorized permissions are granted
