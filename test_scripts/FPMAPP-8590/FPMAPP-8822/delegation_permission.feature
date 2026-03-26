# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8822
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:53:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation permissions for role-based approval workflows
  As an approver
  I want delegation to be allowed only for roles with delegation permissions
  So that unauthorized delegation attempts are blocked and logged

  Background:
    Given the system has roles configured with and without delegation permissions
    And there are approval requests pending for roles with and without delegation rights

  Scenario: Successful delegation by a role with delegation permission
    Given I am logged in as a user with role "Manager" who has delegation permission
    And I have an approval request with ID "1001" pending for my role
    When I attempt to delegate the approval request to user with ID "3"
    Then the delegation should be successful
    And the delegation action should be recorded with timestamp and approver role

  Scenario: Delegation denied for a role without delegation permission
    Given I am logged in as a user with role "Analyst" who does NOT have delegation permission
    And I have an approval request with ID "2001" pending for my role
    When I attempt to delegate the approval request to user with ID "4"
    Then the delegation should be denied
    And an appropriate error message "Delegation not allowed for your role" should be shown
    And no delegation action should be recorded
