# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8931
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:58:52
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Delegation with Controlled Permissions and Audit Logging
  
  As an approver
  I want to delegate my approval authority with defined permissions
  So that the delegate can perform approvals and all actions are logged

  Background:
    Given the user "approverUser" is logged in as an approver
    And the delegate user "delegateUser" exists and is eligible to receive delegated approval rights
    And the system supports role-based permissions

  Scenario: Successfully delegate approval rights with specific permissions
    When the approver navigates to the approval delegation interface
    And selects the delegate user "delegateUser"
    And assigns the following approval permissions:
      | permission         |
      | APPROVE_INVOICES   |
      | APPROVE_BUDGETS    |
    And confirms and saves the delegation
    Then the delegation is saved successfully
    And the delegate user "delegateUser" receives the assigned approval permissions
    And the delegation action is logged with timestamp and user details
    And the delegate user "delegateUser" can perform approvals according to assigned permissions

  Scenario Outline: Delegate user attempts to perform approval actions
    Given the delegate user "<delegateUser>" is logged in
    When the delegate navigates to the approval page
    Then the delegate should be able to perform the following approval actions:
      | action           |
      | APPROVE_INVOICES |
      | APPROVE_BUDGETS  |

    Examples:
      | delegateUser   |
      | delegateUser   |
