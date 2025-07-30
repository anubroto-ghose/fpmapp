# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4609
# Epic: BANK-4579
# Generated on: 2025-07-30 05:07:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Status Change Email Notification

  Scenario: Successful email notification on approval status change
    Given the user is registered and has an active email account
    When the user submits a request for approval
    And the status of the request is changed to 'Approved'
    Then the user should receive an email notification
    And the email notification should be formatted correctly with the subject containing 'Approval Status Changed'
    And the email body should contain 'Your request has been approved.'
    And the email body should contain comments from the approver
