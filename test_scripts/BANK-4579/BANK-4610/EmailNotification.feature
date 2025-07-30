# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4610
# Epic: BANK-4579
# Generated on: 2025-07-30 05:07:12
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email Notification on Approval Status Change

  Scenario: Successful email notification on approval status change
    Given the user is registered with a valid email address
    When the approval status is changed
    Then the user should receive an email notification with the new approval status and comments from the approver
