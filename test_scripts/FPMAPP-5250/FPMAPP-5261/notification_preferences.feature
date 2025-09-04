# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5261
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:51:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification Preference Settings
  As an approver
  I want to configure my notification preferences for approvals and rejections
  So that I receive updates via email and/or in-app notifications

  Background:
    Given a user is logged in as an approver

  Scenario: Update notification preferences to enable both email and in-app notifications
    When the user navigates to the notification preferences page
    And the user selects both email and in-app notification options
    And the user saves the notification preferences
    Then the system should save the notification preferences successfully
    And the user should see a confirmation message indicating preferences were saved
