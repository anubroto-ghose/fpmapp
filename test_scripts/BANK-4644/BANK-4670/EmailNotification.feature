# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4670
# Epic: BANK-4644
# Generated on: 2025-07-30 16:58:44
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email Notification for Pending Approvals

  Scenario: User receives email notification for pending approval
    Given a user has submitted a dealsheet for approval
    When the email notification is sent
    Then the user should receive an email notification regarding the pending approval of the dealsheet
