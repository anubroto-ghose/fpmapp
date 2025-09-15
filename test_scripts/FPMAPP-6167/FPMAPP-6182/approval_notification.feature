# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6182
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:10:41
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time in-app notifications on approval status changes
  
  Background:
    Given the user "alice.bankuser" is logged into the FPM UI
    And the notification framework is enabled and functioning
    And the approval controller is integrated with in-app notification logic

  @high_priority
  Scenario: User receives real-time notification upon approval status update
    When an approval status change event is triggered for approval id "1001" with status "Approved"
    Then the user sees an in-app notification with details:
      | approvalId | 1001 |
      | status     | Approved |
    And the notification appears promptly without page refresh
    And the user can dismiss the notification
