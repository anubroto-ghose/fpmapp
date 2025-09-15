# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6217
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:41:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation of Approval Rights
  As an approver with delegation privileges
  I want to delegate approval rights only if authorized
  So that delegation actions are correctly logged in audit trail and unauthorized delegations are blocked

  Background:
    Given delegation functionality is enabled with role restrictions
    And audit logging is configured for delegation activities
    And the following users exist:
      | username            | roles                      |
      | authorized.approver  | ROLE_APPROVAL_DELEGATOR    |
      | unauthorized.user   | ROLE_VIEWER                |
      | delegatee.user      | ROLE_APPROVER              |

  @HighPriority
  Scenario Outline: Delegation attempts based on user role
    When user "<delegator>" attempts to delegate approval rights for approval ID "APPROVAL-12345" to user "delegatee.user" for duration 3600 seconds
    Then the delegation should be <outcome>
    And audit logs for approval ID "APPROVAL-12345" should <audit_expectation>

    Examples:
      | delegator           | outcome  | audit_expectation                |
      | authorized.approver | succeed  | contain delegation entry        |
      | unauthorized.user   | fail     | not contain delegation entry    |
