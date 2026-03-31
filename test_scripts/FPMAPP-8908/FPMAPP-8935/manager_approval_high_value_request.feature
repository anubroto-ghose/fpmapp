# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8935
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:54:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manager approval blocked for high-value financial requests
  
  As a financial approver with the role of manager
  I want to be prevented from approving financial requests above my approval threshold
  So that the role-based hierarchical approval workflow is enforced correctly

  Background:
    Given the role mappings and approval thresholds are configured
      | role    | max_approval_amount |
      | manager | 99999.99            |
      | director| 1000000.00          |
    And a user with username "managerUser" and role "manager" exists and is active
    And a financial request with amount above 100000.00 exists

  Scenario: Manager attempts to approve a high-value financial request
    When the manager submits a financial approval request with amount 150000.00
    And the manager attempts to approve the high-value request
    Then the system prevents the manager from approving the request
    And the request status remains "PENDING" or is routed to "Director"
    And the approval status does not update incorrectly
    And the role hierarchy enforcement prevents bypass
