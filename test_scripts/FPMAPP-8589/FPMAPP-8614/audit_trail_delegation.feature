# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8614
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:04:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Delegation Enhancements
  As an auditor
  I want comprehensive audit trails for all approval-related actions
  So that delegation info is captured and data integrity is maintained across services

  Background:
    Given the delegation feature is enabled and configured
    And a user "john.manager" exists with approval rights
    And a user "jane.delegate" exists

  Scenario: Delegate approval rights and verify audit trail
    When "john.manager" delegates approval rights to "jane.delegate"
    And "jane.delegate" performs an approval action on dealsheet "12345"
    Then the audit log entry for dealsheet "12345" should include delegation_info linking "john.manager" and "jane.delegate"
    And the user metadata should reflect "jane.delegate" as the performer
    And the audit log timestamps should be accurate and consistent
    And audit logs across services should be consistent and maintain data integrity

  @delegation
  Scenario Outline: Delegated user approval audit trail verification
    Given the delegation feature is enabled
    When <delegator> delegates approval rights to <delegatee>
    And <delegatee> approves dealsheet <dealsheetId>
    Then the audit log for dealsheet <dealsheetId> contains delegation_info with delegator <delegator> and delegatee <delegatee>
    And the audit log user metadata is <delegatee>
    And the audit log timestamps are valid and consistent

    Examples:
      | delegator     | delegatee      | dealsheetId |
      | john.manager  | jane.delegate  | 12345       |
      | alice.lead    | bob.assistant  | 67890       |
