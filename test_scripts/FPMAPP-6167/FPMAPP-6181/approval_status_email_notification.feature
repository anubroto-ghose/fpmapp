# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6181
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:11:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval status email notification
  In order to keep all parties informed about approval changes
  As a user involved in the approval process
  I want an email notification sent automatically when the approval status changes

  Background:
    Given the user "approvalUser1" is logged in
    And the user has an assigned approval task with ID "12345"
    And the SMTP email server is configured and operational

  Scenario: Change approval status from pending to approved triggers email notification
    When the user navigates to the approval task with ID "12345"
    And the approval status is "Pending"
    And the user changes the approval status to "Approved"
    Then the approval status should be updated to "Approved"
    And an email notification should be sent to the approver's email
    And the email content should include "Approval Status Update" and "approved"

  @negative
  Scenario: Attempting to change approval status without SMTP configured
    Given the SMTP email server is not operational
    When the user changes the approval status to "Approved"
    Then an error notification about email sending failure should be displayed
