# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6185
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:07:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation capability restricted and audit logged for approval workflows
  
  As an approver
  I want hierarchical role-based approval workflows with delegation restricted to authorized roles
  So that only authorized users can delegate approval authority and all delegation actions are audit logged

  Background:
    Given the delegation roles and permissions are configured
    And audit logging is enabled for delegation actions

  Scenario: Successful delegation by an approver with authorized delegation rights
    Given I am logged in as a user with delegation rights
    When I submit a delegation request for approval id "12345" to delegate to user "2001" for "7" days
    Then the system allows the delegation
    And the delegation is recorded in the audit logs with user id and timestamp

  Scenario: Delegation attempt by an approver without delegation rights
    Given I am logged in as a user without delegation rights
    When I attempt to submit a delegation request for approval id "12345" to delegate to user "2001" for "7" days
    Then the system rejects the delegation
    And the rejection is logged in the audit logs with user id and timestamp

  Scenario: Audit logs provide clear traceability of delegation events
    Given the delegation has been performed and attempts made
    When I retrieve the audit logs for approval id "12345"
    Then I should see audit records for delegation assignments and delegation denials with accurate user information and timestamps
