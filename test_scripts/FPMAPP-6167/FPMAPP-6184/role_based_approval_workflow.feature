# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6184
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:08:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based hierarchical approval workflows for Deal Sheet, Staffing, and Travel Requests
  
  Background:
    Given the hierarchical roles "Manager" and "Director" are configured with approval thresholds
    And the approval workflows for deal sheets, staffing, and travel requests are active

  Scenario Outline: Approval routing respects hierarchical role thresholds for different request types
    When a user submits a <RequestType> request with amount <Amount>
    Then the approval request is routed to a <ExpectedApprover> with status "PENDING_APPROVAL"

    Examples:
      | RequestType    | Amount  | ExpectedApprover |
      | Deal Sheet     | 75000   | Director         |
      | Deal Sheet     | 30000   | Manager          |
      | Staffing       | 15000   | Manager          |
      | Staffing       | 35000   | Director         |
      | Travel Request | 12000   | Director         |
      | Travel Request | 8000    | Manager          |

  Scenario: Director level requests bypass manager approvals
    When a user submits a Deal Sheet request with amount 100000
    Then the approval request is routed directly to Director
    And the system reflects no intermediate Manager approval

  Scenario: Manager level requests do not route unnecessarily to Directors
    When a user submits a Staffing request with amount 10000
    Then the approval request is routed to Manager
    And the system confirms no Director approvals are initiated

  Scenario: Approval routing consistency across all request types
    Given the following requests are submitted:
      | RequestType    | Amount  |
      | Deal Sheet     | 45000   |
      | Staffing       | 25000   |
      | Travel Request | 9000    |
    Then the routing for each is:
      | RequestType    | Approver |
      | Deal Sheet     | Manager  |
      | Staffing       | Director |
      | Travel Request | Manager  |
