# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8885
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:34:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Prevent delegation to unauthorized users
  As an approver
  I want to delegate my approval authority only to eligible users
  So that unauthorized delegation is prevented and audit integrity is maintained

  Background:
    Given the approver "approverUser" is logged into the system
    And the user "unauthorizedUser" does not have delegation eligibility

  Scenario: Attempt to delegate approval to an unauthorized user
    When the approver navigates to the delegation management interface
    And attempts to select "unauthorizedUser" as delegate
    And defines a delegation period from tomorrow to 7 days later
    And submits the delegation request
    Then the system prevents delegation to "unauthorizedUser"
    And an error message is displayed indicating lack of permissions
    And no delegation action is logged
    And no notification is sent to "unauthorizedUser"
