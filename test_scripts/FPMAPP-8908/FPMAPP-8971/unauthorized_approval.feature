# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8971
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:32:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Unauthorized Approval Prevention
  As a financial planning system
  I want to prevent users without the correct approver role from approving requests
  So that approval status and approver roles remain consistent and secure

  Background:
    Given an approval request with ID "1001" is pending approval
    And the approval request has current approver role "ROLE_APPROVER"
    And a user "unauthorizedUser" exists without approval rights

  Scenario: Unauthorized user attempts to approve a request
    When the user "unauthorizedUser" attempts to approve the approval request "1001"
    Then the approval status for request "1001" remains "PENDING"
    And the current approver role for request "1001" remains "ROLE_APPROVER"
    And the system returns an error message "User does not have approval rights"
    And the approval action is rejected
