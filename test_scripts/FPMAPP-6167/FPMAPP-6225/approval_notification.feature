# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6225
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:36:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Notification
  As an approver
  I want to receive in-app and email notifications upon new approval assignment
  So that I can promptly attend to my approval tasks

  Background:
    Given the SMTP mail service is active
    And the in-app UI notification service is active
    And approver "approverC" has notification settings enabled

  Scenario: Approver receives notifications upon new approval task assignment
    When an approval routing event assigns a pending task to approver "approverC"
    Then approver "approverC" should receive an in-app notification immediately
    And approver "approverC" should receive an email notification confirming the new approval task
    And the notification content references the deal sheet or approval request
    And notifications are delivered without manual intervention
