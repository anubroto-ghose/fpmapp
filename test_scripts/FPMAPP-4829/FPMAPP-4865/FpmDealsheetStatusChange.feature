# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4865
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:13:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification on Status Change

  Scenario: Verify notifications upon status changes
    Given the user has submitted a request and is subscribed to notifications
    When the admin changes the status of the request to Approved
    Then the requestor should receive a notification about the status change
