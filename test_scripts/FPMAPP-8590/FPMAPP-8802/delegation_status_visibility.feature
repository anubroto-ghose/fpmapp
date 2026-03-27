# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8802
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:00:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Status Visibility
  As a manager (delegator) and authorized user (delegatee)
  I want to view and verify delegation status in the UI
  So that approval rights delegation is transparent and auditable

  Background:
    Given a delegation record exists between "manager1" and "user2"
    And both users "manager1" and "user2" are logged in

  Scenario: Delegator views delegation status
    When the delegator "manager1" views the delegation status page
    Then the delegation status should be "Active"
    And the status message "Delegation is active" should be displayed

  Scenario: Delegatee views delegation status
    When the delegatee "user2" views the delegation status page
    Then the delegation status should be "Active"
    And the status message "Delegation is active" should be displayed

  Scenario: Delegator revokes delegation and both users refresh
    When the delegator "manager1" revokes the delegation
    And both users refresh their delegation status pages
    Then the delegation status for "manager1" should be "Revoked"
    And the delegation status for "user2" should be "Revoked"
    And the status message "Delegation has been revoked." should be displayed for both users
    And the audit log should record the revocation event
