# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5268
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:40:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow for Deal Sheets
  As a financial manager
  I want to approve deal sheet requests according to my assigned role
  So that approval processes are properly managed with delegation and notifications

  Background:
    Given the user "financeManagerUser" is logged in with role "FinancialManager"

  Scenario: Approve a deal sheet request successfully
    Given the user navigates to the approvals section
    And a deal sheet request with id "1001" exists and is pending approval
    When the user selects the deal sheet request "1001" to approve
    And the user confirms the approval
    Then the deal sheet request "1001" is marked as approved
    And a notification of approval is sent to the requester

  Scenario: Attempt to approve a deal sheet request without approval rights
    Given the user "readOnlyUser" is logged in with role "ReadOnly"
    And the user navigates to the approvals section
    When the user attempts to approve the deal sheet request "1001"
    Then the approval is declined with a permission error message

  Scenario: Approval with delegation
    Given the user "delegateUser" is logged in with role "Delegate"
    And delegation from "financeManagerUser" is active
    And the user navigates to the approvals section
    When the user approves the deal sheet request "1001" on behalf of "financeManagerUser"
    Then the deal sheet request "1001" is marked as approved originating from delegation
    And a notification of approval including delegation info is sent to the requester