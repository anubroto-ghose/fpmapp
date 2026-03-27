# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8650
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:40:11
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Workflow Delegation and Notification
  
  As an approver
  I want hierarchical role-based approval workflows with delegation and notification
  So that approval status updates include delegation info and trigger notifications

  Background:
    Given approval workflows are configured with delegation and notification features enabled
    And email (SMTP) and in-app notification systems are operational

  Scenario: Approve an approval request with delegation involved
    When I approve an approval request with delegation
    Then the approval status update should include delegation information and timestamps
    And email notifications should be sent to relevant users
    And in-app notifications should be triggered for the approval and delegation actions
    And audit logs should capture approval, delegation, and notification entries with user details and timestamps
