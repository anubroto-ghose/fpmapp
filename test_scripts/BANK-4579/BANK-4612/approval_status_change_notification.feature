# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4612
# Epic: BANK-4579
# Generated on: 2025-07-30 05:05:51
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status Change Notification

  Scenario: User receives notification for approval status change
    Given the user has a valid email
    When an approval status change occurs
    Then the system logs the notification event
    And the log contains an entry for the notification sent
    | Field        | Value                  |
    | User Email  | user@example.com      |
    | New Status  | Approved              |
    | Comments     | Approved by admin     |