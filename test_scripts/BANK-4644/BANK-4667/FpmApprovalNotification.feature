# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4667
# Epic: BANK-4644
# Generated on: 2025-07-30 16:59:32
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email notification for rejected approval status

  Scenario: Finance manager receives notification for rejected approval
    Given the finance manager has a financial entry with a pending approval status
    When the approval status of the financial entry is changed to 'rejected'
    Then the finance manager should receive an email notification indicating that the financial entry has been rejected