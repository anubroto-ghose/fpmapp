# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8800
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:02:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Management
  As a manager
  I want to delegate my approval rights to authorized users with controlled rules
  So that delegation is secure and auditable

  Background:
    Given the application is running

  @AuthorizedUser
  Scenario: Authorized user successfully delegates approval rights
    Given I am logged in as a user with role "MANAGER"
    And I navigate to the Delegation Management page
    When I delegate approval rights to user "validDelegatee" from "2024-07-01" to "2024-07-31"
    Then I should see a confirmation message "Delegation successfully created"
    And the delegation status should be "Active"
    And the delegation action should be logged with full audit details

  @UnauthorizedUser
  Scenario: Unauthorized user is blocked from delegating approval rights
    Given I am logged in as a user with role "STAFF"
    And I navigate to the Delegation Management page
    When I attempt to delegate approval rights to user "someDelegatee"
    Then I should see an error message "You are not authorized to delegate approval rights"
    And no delegation should be created
    And no audit log should be recorded
