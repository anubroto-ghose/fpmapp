# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6194
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:00:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based Delegation Eligibility and Audit Logging
  
  As an approver
  I want delegation to be allowed only for specified roles
  So that all delegation actions are logged comprehensively

  Background:
    Given the following users exist with roles and delegation eligibility:
      | userId          | role            | isDelegationEligible |
      | user_eligible   | ApproverLevel1   | true                 |
      | user_ineligible | ApproverLevel2   | false                |
      | user_delegatee  | ApproverLevel3   | false                |

    And the approval item with ID "12345" is assigned to "user_eligible" and "user_ineligible"

  Scenario: Eligible user delegates an assigned approval task successfully
    Given I am logged in as user "user_eligible"
    When I attempt to delegate approval "12345" to user "user_delegatee" for 60 minutes
    Then the delegation should succeed
    And an audit log entry of type "DELEGATION_ASSIGNED" should be recorded for user "user_eligible"

  Scenario: Ineligible user attempts delegation and is blocked
    Given I am logged in as user "user_ineligible"
    When I attempt to delegate approval "12345" to user "user_delegatee" for 60 minutes
    Then the delegation should fail with error message "User role not eligible for delegation."
    And an audit log entry of type "DELEGATION_FAILED" should be recorded for user "user_ineligible"

  Scenario: Audit log contains accurate delegation entries
    When I retrieve audit log entries for approval "12345"
    Then the audit log should contain an entry of type "DELEGATION_ASSIGNED" by user "user_eligible"
    And the audit log should contain an entry of type "DELEGATION_FAILED" by user "user_ineligible"
