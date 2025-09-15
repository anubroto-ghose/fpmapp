# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6214
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:44:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based approval notifications failure handling
  
  As a user involved in approvals
  I want the system to handle email and in-app notification failures gracefully
  So that approval workflows complete without crash and errors are retried and logged

  Background:
    Given the email SMTP service is unavailable
    And the in-app notification service is failing

  @NotificationFailure
  Scenario: Approval status changes triggers notification failures
    When I trigger an approval status change for deal sheet with ID 12345 to status "APPROVED"
    Then the system should not crash the approval workflow
    And an email notification sending attempt is made and fails
    And an in-app notification sending attempt is made and fails
    And error logs capture failure details about email and in-app notifications
    And the system triggers retry mechanisms for notifications
    And the user interface shows the updated approval status "APPROVED"
    And the user is informed about notification delivery delays or retries via UI
