# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4611
# Epic: BANK-4579
# Generated on: 2025-07-30 05:06:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email Notification Opt-Out

  Scenario: User does not receive email notification for approval status change
    Given the user has opted out of email notifications
    When an approval status change is triggered
    Then the user should not receive any email notification regarding the approval status change
