# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8932
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:57:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit log captures delegation and approval events immutably
  
  As an approver
  I want to delegate my approval authority with controlled permissions
  And have all delegation actions logged immutably

  Background:
    Given a delegation has been created and saved
    And approvals and rejections are performed by both the original approver and the delegate

  @high
  Scenario: Delegation actions are logged with accurate timestamps and user details
    When the original approver performs a delegation action "create" for delegate "delegate1"
    And the original approver performs a delegation action "modify" for delegate "delegate1"
    And the original approver performs a delegation action "revoke" for delegate "delegate1"
    Then the audit log should contain delegation actions "create", "modify", and "revoke" by "approver1" with accurate timestamps

  @high
  Scenario: Approval and rejection events are logged immutably by original approver and delegate
    When the original approver performs an approval action "approve"
    And the original approver performs an approval action "reject"
    And the delegate performs an approval action "approve"
    And the delegate performs an approval action "reject"
    Then the audit log should contain approval and rejection events by both "approver1" and "delegate1" immutably

  @high
  Scenario: Audit trail shows complete, unaltered history of delegation and approval events
    When the user accesses the audit trail logs
    Then the audit trail should show a complete and unaltered history of delegation and approval events

  @high
  Scenario: Audit logs are accessible only to authorized users
    When an unauthorized user attempts to access the audit trail logs
    Then access should be denied
