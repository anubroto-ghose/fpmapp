# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6195
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:59:18
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Multi-Level Role-Based Approval Workflow with Automatic Escalation and Notifications
  
  As an approver
  I want a role-based, hierarchical approval workflow for deal sheets, staffing, and travel requests
  So that approval requests exceeding thresholds are escalated according to hierarchy, and notifications are properly dispatched

  Background:
    Given a multi-level approval hierarchy is configured
    And an approval request exceeding the lowest-level approver's threshold exists

  Scenario: Submit approval request requiring multiple approval levels
    When a submitter submits an approval request with amount 60000.00
    Then the lowest level approver receives the approval task first

  Scenario: Automatic escalation after no action within timeout
    Given the approval task is assigned to the lowest level approver
    When no action is taken within the escalation timeout period
    Then the approval task is automatically escalated to the next level approver

  Scenario: Top level approves after escalation
    Given the approval task is escalated to the top level approver
    When the top level approver approves the request
    Then the approval status updates to "Approved"
    And notifications are triggered for the approval

  Scenario: Verify controller handles status updates and notifications without errors
    Given an approval is approved at any level
    When the system processes status update
    Then the approval status is correctly persisted
    And notification dispatch completes successfully
