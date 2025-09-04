# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5268
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:16:32
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow for Deal Sheets
  
  As a financial manager
  I want to approve deal sheet requests according to my role-based approval rights
  So that approval workflows follow hierarchical business rules and proper notifications are sent

  Background:
    Given a user "finance.manager@example.com" with role "FinancialManager" is logged in
    And there is an existing deal sheet request with ID "request-1234" submitted by user "deal.requester@example.com" with status "Pending"

  Scenario: Approving a deal sheet successfully as a financial manager
    When the user navigates to the approvals section
    And selects the deal sheet request with ID "request-1234"
    And clicks the "Approve" button
    Then the approval is successfully processed
    And the requester receives a notification of approval

  Scenario: Attempting approval without proper role
    Given a user "junior.staff@example.com" with role "JuniorStaff" is logged in
    When the user navigates to the approvals section
    And selects the deal sheet request with ID "request-1234"
    And clicks the "Approve" button
    Then the approval is rejected due to insufficient permissions
    And the user sees an error message "You do not have permission to approve this request."
