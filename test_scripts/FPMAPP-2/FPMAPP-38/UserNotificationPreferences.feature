# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-38
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:46:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: User Notification Preferences

  Scenario: User updates notification preferences
    Given the user is logged into the application
    When the user navigates to the settings/preferences section
    And the user changes notification preferences for email and in-app notifications
    And the user saves the changes
    Then the notification preferences should be updated successfully without errors
