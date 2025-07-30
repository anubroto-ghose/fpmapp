# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4608
# Epic: BANK-4579
# Generated on: 2025-07-30 05:08:43
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status Change Notifications

  Scenario: No email notification on disapproval status change
    Given the user is registered and has an active email account
    When the user submits a request for approval
    And the status of the request is changed to 'Disapproved'
    Then the user does not receive an email notification indicating the status change to 'Disapproved'
