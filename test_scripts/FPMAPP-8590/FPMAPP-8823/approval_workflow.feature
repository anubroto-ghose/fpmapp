# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8823
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:47:18
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-based hierarchical approval workflow
  As an approver
  I want role-based hierarchical approval workflows with financial thresholds
  So that approval statuses reflect detailed states and record timestamps and roles

  Background:
    Given the approval workflow system is active with status tracking enabled
    And there are approval requests submitted and pending

  Scenario: Approve an approval request and verify status changes to 'approved'
    When I approve an approval request
    Then the approval status should be "Approved"
    And the approver role and timestamp should be recorded

  Scenario: Reject an approval request and verify status changes to 'rejected'
    When I reject an approval request
    Then the approval status should be "Rejected"
    And the approver role and timestamp should be recorded

  Scenario: Delegate an approval request and verify status changes to 'delegated'
    Given I have delegation permission
    When I delegate an approval request
    Then the approval status should be "Delegated"
    And the approver role and timestamp should be recorded

  Scenario: View the current approval status from a user perspective
    When I view the current approval status of an approval request
    Then I should see the current approval status reflecting the hierarchical workflow
