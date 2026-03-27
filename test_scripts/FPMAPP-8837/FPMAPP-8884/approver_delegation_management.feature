# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8884
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:34:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approver Delegation Management
  As an approver
  I want to delegate my approval authority to another user with controlled permissions and audit logging
  So that the delegate user can perform approval actions temporarily

  Background:
    Given the approver "approverUser" is logged into the system
    And the delegate user "delegateUser" exists and is eligible for delegation
    And the approver has valid approval rights

  Scenario: Successful delegation of approval rights with valid user and time period
    When the approver navigates to the delegation management interface
    And selects the delegate user "delegateUser"
    And defines a valid delegation time period from "2024-07-01" to "2024-07-07"
    And submits the delegation request
    Then the delegation is successfully created
    And the delegate user receives a notification about the temporary approval rights
    And the delegation action is logged with user details and timestamps
    And the delegate user can perform approval actions within the delegated time period
