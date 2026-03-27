# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8617
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:02:20
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification Failure Handling and Audit Logging
  
  As an approver or requester
  I want the system to handle notification failures gracefully
  So that approval requests continue processing and failures are logged for audit

  Background:
    Given the SMTP server is misconfigured or unavailable
    And I am logged in as an approver
    And I have access to the approval request submission functionality

  Scenario: Submit approval request with notification failure
    When I submit a new approval request with project name "Corporate Loan Approval - Project Phoenix" and amount "5000000"
    Then the notification delivery status should indicate failure
    And the system should log the notification failure event with relevant details
    And the approval request process should continue without blocking
    And appropriate error handling or retry mechanisms should be triggered
