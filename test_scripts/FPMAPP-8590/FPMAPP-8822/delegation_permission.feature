# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8822
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:47:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation permissions for approval requests
  As an approver
  I want to delegate approval requests only if my role has delegation permissions
  So that unauthorized delegation attempts are blocked and logged

  Background:
    Given the system has roles configured with and without delegation permissions
    And there are approval requests pending for roles with and without delegation rights

  Scenario: Successful delegation by role with delegation permission
    Given I am logged in as a user with delegation permission
    When I attempt to delegate an approval request
    Then the delegation should be successful
    And the delegation action should be recorded with timestamp and approver role

  Scenario: Delegation denied for role without delegation permission
    Given I am logged in as a user without delegation permission
    When I attempt to delegate an approval request
    Then the delegation should be denied
    And an appropriate error message should be shown
    And no delegation action should be recorded
