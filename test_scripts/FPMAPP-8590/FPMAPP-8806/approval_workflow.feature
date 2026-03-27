# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8806
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:58:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Workflow Enforces Hierarchical Role Mapping with Financial Thresholds
  
  As a director or manager
  I want to approve deal sheets, staffing, and travel requests based on financial thresholds
  So that only authorized roles can approve requests within their limits

  Background:
    Given the following users exist:
      | username     | role     | financialThreshold |
      | directorUser | DIRECTOR | 200000            |
      | managerUser  | MANAGER  | 50000             |
      | staffUser    | STAFF    | 0                 |

  Scenario Outline: Approver approves request within financial threshold
    Given the user "<approver>" is logged in
    And a <requestType> request is created with amount <amount>
    When the request is submitted
    Then the request is routed to the correct approver based on role and amount
    When the approver approves the request
    Then the request status updates to "Approved" in real-time
    And the requester can see the updated approval status

    Examples:
      | approver     | requestType | amount  |
      | managerUser  | deal sheet | 30000   |
      | directorUser | travel     | 150000  |

  Scenario: Unauthorized user cannot approve request
    Given the user "staffUser" is logged in
    And a deal sheet request is created with amount 10000
    When the request is submitted
    And the user attempts to approve the request
    Then the approval is denied with an authorization error
    And the request status remains "Pending Approval"
