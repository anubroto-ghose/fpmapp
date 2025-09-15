# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6221
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:39:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Display in Approval Request Details
  As a requester
  I want to see a full audit trail and real-time status tracking
  So that I can verify approval actions, delegations, rejections, and overrides

  Background:
    Given the user is logged in as a requester
    And approval requests exist with actions including approvals, rejections, delegations, and overrides
    And the audit API is available and populated

  Scenario: View complete audit trail on approval request details page
    When the user navigates to the approval request details page with approval id 1001
    And opens the audit trail expandable panel
    Then the audit trail shows a complete, ordered list of actions
    And each record displays a correct timestamp
    And user identity is accurately shown for each audit event
    And delegation and override events are clearly indicated
    And no audit records are missing or incomplete

// Step Definitions for the feature would be implemented in Java under the corresponding package, e.g., com.webapp.fpmapp.steps