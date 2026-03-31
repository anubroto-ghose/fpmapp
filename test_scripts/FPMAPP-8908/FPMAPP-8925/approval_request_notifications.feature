# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8925
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:04:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time feedback and notifications for approval request status changes
  
  As a requester
  I want to receive real-time status updates and notifications for my approval requests
  So that I can stay informed about the approval process

  Background:
    Given the user "testuser" is logged into the FPMApplication
    And the user has at least one pending approval request
    And email notifications are enabled for the user

  Scenario: Submit approval request and receive notifications on status change
    When the user submits a new approval request titled "Budget Increase Request"
    And the approval request status is changed from "Pending" to "Approved"
    Then an in-app notification appears immediately reflecting the status change
    And the user receives an email alert with correct details about the approval request and status update
    And the notification content matches the role-based routing and delegation rules
    And no duplicate or missing notifications occur
