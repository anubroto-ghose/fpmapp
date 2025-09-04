# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5262
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:49:05
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit logging of approval actions
  As a compliance officer
  I want a full audit trail for all approval and rejection actions
  So that compliance and traceability requirements are met

  Background:
    Given the system is running
    And a user "compliance.officer" with role "COMPLIANCE_OFFICER" exists
    And the user "compliance.officer" is logged in

  Scenario: Approval actions are logged in the audit trail
    Given a transaction with id "${transactionId}" exists
    When the compliance officer approves the transaction with id "${transactionId}"
    Then the audit trail should contain an approval action for user "compliance.officer" on the transaction "${transactionId}"
    And the audit entry should have a valid timestamp

  Scenario Outline: Various approval actions logged
    Given a transaction with id "<transactionId>" exists
    When the compliance officer performs the approval action on the transaction with id "<transactionId>"
    Then the audit trail should record the approval with correct user and timestamp

    Examples:
      | transactionId                             |
      | 11111111-1111-1111-1111-111111111111    |
      | 22222222-2222-2222-2222-222222222222    |

