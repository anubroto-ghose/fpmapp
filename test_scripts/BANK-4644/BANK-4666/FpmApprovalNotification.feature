# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4666
# Epic: BANK-4644
# Generated on: 2025-07-30 16:59:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Notification Email

  Scenario: Successful email notification for approval status change
    Given the finance manager has a financial entry with a pending approval status
    When the approval status of the financial entry is changed to 'approved'
    Then the finance manager should receive an email notification indicating that the financial entry has been approved
