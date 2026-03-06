# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-37
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:46:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: In-app notification for pending approvals

  Scenario: User checks for pending approval notifications
    Given the user has submitted an approval request
    And the user is logged into the application
    When the user navigates to the notifications section
    Then an in-app notification should be displayed indicating the pending approval request
