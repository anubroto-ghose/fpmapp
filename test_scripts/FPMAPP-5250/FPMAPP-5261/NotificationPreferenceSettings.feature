# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5261
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:18:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification Preference Settings
  As an approver
  I want to set my notification preferences
  So that I can receive notifications via email and in-app messages

  Background:
    Given I am logged in as an approver with notification settings access

  Scenario: Set email and in-app notifications and save
    When I navigate to the notification preferences page
    And I select email notifications
    And I select in-app notifications
    And I save the notification preferences
    Then I should see a confirmation message confirming the preferences were saved
    And both email and in-app notification options should be selected