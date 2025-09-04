# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5260
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:18:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: In-App Notification on Request Rejection
  As an approver
  I want to receive an in-app notification when I reject a pending request
  So that I am aware of the rejection status immediately

  Background:
    Given the user is logged in as an approver
    And the user has a request pending approval

  Scenario: Approver rejects a pending request and receives notification
    When the approver rejects the pending request with ID "12345"
    Then the approver should receive an in-app notification with title containing "Request #12345 has been rejected"
    And the notification should include relevant request details

  Scenario Outline: Multiple request rejection notifications
    Given the approver has pending request with ID "<requestId>"
    When the approver rejects the pending request with ID "<requestId>"
    Then the approver should receive an in-app notification with title containing "Request #<requestId> has been rejected"
    And the notification should include relevant request details

    Examples:
      | requestId |
      | 12345    |
      | 67890    |
