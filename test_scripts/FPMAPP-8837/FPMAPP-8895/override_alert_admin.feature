# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8895
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:27:19
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Override Alert Display for Admin Users
  As an admin user
  I want to receive real-time override alerts prominently displayed in the UI
  So that I can take immediate action on approval overrides

  Background:
    Given the application is running
    And a user is logged in with admin role

  @override-alert
  Scenario: Override alert is displayed prominently to admin users
    Given an override event occurs on an approval request
    When the admin user views the alerts page
    Then the override alert should be displayed immediately
    And the alert should be visually prominent
    And the alert should contain relevant override details

  @override-alert
  Scenario: Non-admin users do not see override alerts
    Given an override event occurs on an approval request
    And a user is logged in with a non-admin role
    When the user views the alerts page
    Then the override alert should not be visible
