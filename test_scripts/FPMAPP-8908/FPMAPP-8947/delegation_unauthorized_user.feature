# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8947
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:43:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Management - Unauthorized User Rejection
  As an approver
  I want to delegate my approval authority to another user with controlled permissions
  So that unauthorized delegation attempts are rejected

  Background:
    Given the approver "approverUser" is logged into the system

  Scenario: Attempt to delegate approval rights to an unauthorized user
    Given the delegate user "unauthorizedUser" does not meet role or delegation rules
    When the approver navigates to the delegation UI
    And attempts to assign approval rights to the delegate user
    And submits the delegation request
    Then the system validates delegation permissions against role and delegation rules
    And the delegation request is rejected
    And an appropriate error message "Delegation request rejected: User does not meet role or delegation rules." is displayed
    And no delegation flags are set
    And no audit log entry is created for the failed delegation attempt
