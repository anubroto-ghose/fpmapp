# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6215
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:43:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Hierarchical Approval Workflows
  
  Background:
    Given the system has users with defined roles:
      | username       | role      |
      | managerUser    | Manager   |
      | directorUser   | Director  |
      | juniorApprover | Approver  |
    And approval workflow thresholds are configured for roles
    And the following pending approval requests exist:
      | type         | approvalId   | requiredRole | approvalStages                |
      | dealsheet    | DEALSHEET-001| Manager      |                             |
      | staffing     | STAFFING-001 | Director     |                             |
      | travel       | TRAVEL-001  | Manager;Director | Multi-tier manager then director approval |

  Scenario: Submit and approve deal sheet request requiring Manager approval
    When "managerUser" submits deal sheet approval request "DEALSHEET-001"
    Then the request "DEALSHEET-001" should be pending Manager approval
    When "juniorApprover" attempts to approve deal sheet "DEALSHEET-001"
    Then the approval should be rejected with message "User does not have required role"
    When "managerUser" approves deal sheet "DEALSHEET-001"
    Then the approval status of "DEALSHEET-001" should be "Approved"

  Scenario: Submit and approve staffing approval request requiring Director approval
    When "directorUser" submits staffing approval request "STAFFING-001"
    Then the request "STAFFING-001" should be pending Director approval
    When "managerUser" attempts to approve staffing "STAFFING-001"
    Then the approval should be rejected with message "User does not have required role"
    When "directorUser" approves staffing "STAFFING-001"
    Then the approval status of "STAFFING-001" should be "Approved"

  Scenario: Submit and approve travel request requiring hierarchical approval
    When "managerUser" submits travel approval request "TRAVEL-001"
    Then the request "TRAVEL-001" should be pending Manager and Director approval sequentially
    When "juniorApprover" attempts to approve travel request "TRAVEL-001"
    Then the approval should be rejected with message "User does not have required role"
    When "managerUser" approves travel request "TRAVEL-001"
    Then the approval status of "TRAVEL-001" should be "ManagerApproved"
    When "directorUser" approves travel request "TRAVEL-001"
    Then the approval status of "TRAVEL-001" should be "DirectorApproved"

  Scenario: Approvals update audit trail correctly
    Given approval "DEALSHEET-001" was approved by "managerUser"
    And approval "STAFFING-001" was approved by "directorUser"
    And approval "TRAVEL-001" was approved by "managerUser" then by "directorUser"
    When the audit trail for approval "DEALSHEET-001" is retrieved
    Then it should contain an approval action by "managerUser" with role "Manager"
    When the audit trail for approval "STAFFING-001" is retrieved
    Then it should contain an approval action by "directorUser" with role "Director"
    When the audit trail for approval "TRAVEL-001" is retrieved
    Then it should contain approval actions by "managerUser" and "directorUser" in correct order
