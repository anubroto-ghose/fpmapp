# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5259
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:53:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Email Notification
  
  As an approver
  I want to receive email notifications when I approve a request
  So that I am informed about the approval status and details

  Background:
    Given the user "approverUser" is logged in with role "Approver"
    And there is a pending approval request with id "REQ123" assigned to "approverUser"

  Scenario: Approver receives an email notification upon approving a request
    When the user approves the request with id "REQ123"
    Then an email notification is sent to "approverUser@example.com" with subject containing "Approval Notification"
    And the email body contains the request id "REQ123"
