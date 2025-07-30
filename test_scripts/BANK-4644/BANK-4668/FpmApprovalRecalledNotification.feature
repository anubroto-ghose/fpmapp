# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4668
# Epic: BANK-4644
# Generated on: 2025-07-30 16:59:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email Notification for Recalled Approval Status

  Scenario: Finance manager receives notification for recalled approval
    Given the finance manager has an approved financial entry
    When the approval status of the financial entry is changed to "recalled"
    Then the finance manager should receive an email notification indicating that the financial entry has been recalled
