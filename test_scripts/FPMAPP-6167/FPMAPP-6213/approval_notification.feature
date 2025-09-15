# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6213
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:45:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time in-app notification upon approval status update

  As a user involved in approval workflows
  I want to see immediate in-app notification when an approval status changes
  So that I am informed promptly about status changes with correct details and navigation

  Background:
    Given the user "approverUser" is logged into FPM_UI and is part of an approval group
    And the in-app notification framework is integrated and active

  Scenario: Display real-time in-app notification after approval status update
    When the approval status for approval request "deal123" is changed to "APPROVED" via the approval controller
    Then a real-time in-app notification should appear immediately
    And the notification should display correct and clear information regarding the approval alert
    When the user clicks the in-app notification
    Then the user is routed to the detailed view page for approval request "deal123"
    And there should be no delays or missed notifications
