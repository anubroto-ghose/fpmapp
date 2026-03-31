# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8972
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:32:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Reject unauthorized approval attempts
  As an Employee user
  I want to be prevented from approving mid-tier approval requests
  So that only authorized roles can approve requests

  Background:
    Given the following users exist:
      | username     | role     |
      | employeeUser | Employee |
      | managerUser  | Manager  |
      | directorUser | Director |
    And the following approval requests exist:
      | requestId | approvalLevel |
      | 1001      | mid-tier      |

  Scenario: Employee role attempts to approve a mid-tier approval request and is rejected
    Given I am logged in as "employeeUser"
    When I attempt to approve approval request with ID "1001"
    Then I should see an authorization error message
    And the approval should be rejected

