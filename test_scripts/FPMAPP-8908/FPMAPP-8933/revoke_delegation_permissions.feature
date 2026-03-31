# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8933
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:56:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Revoke Delegation Permissions and Verify Access Removal
  
  As an approver
  I want to revoke delegation permissions from a delegate user
  So that the delegate user loses approval authority and the action is logged

  Background:
    Given a delegation exists with active permissions granted to "delegateUser"
    And the user "approverUser" is authorized to revoke delegation

  Scenario: Successfully revoke delegation permissions and verify delegate access removal
    When the user "approverUser" logs into the system
    And navigates to the delegation management interface
    And selects the delegate user "delegateUser"
    And revokes the delegation permissions
    And saves the changes
    Then the system should confirm that delegation permissions are revoked successfully
    And the revocation action is logged with timestamp and user details
    When the delegate user "delegateUser" attempts to perform approval actions
    Then the delegate user should be denied approval permissions
    And the system enforces role-based approval rules post-revocation

  Scenario Outline: Attempt to revoke delegation permissions without authorization
    Given the user "<user>" is not authorized to revoke delegation
    When the user "<user>" logs into the system
    And navigates to the delegation management interface
    Then the user should see an authorization error message

    Examples:
      | user          |
      | unauthorizedUser |
