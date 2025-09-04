# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5417
# Epic: FPMAPP-5363
# Generated on: 2025-09-04 16:16:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval routing based on financial thresholds

  Background:
    Given the approval workflows and roles with financial thresholds are configured

  @tier1
  Scenario: Approval request with amount within Tier 1 threshold
    Given a user submits an approval request with amount 8000
    When the system processes the approval request
    Then the task is assigned to an approver with role "Tier1ApproverRole"
    And no other approvers outside the threshold have the task

  @tier2
  Scenario: Approval request with amount within Tier 2 threshold
    Given a user submits an approval request with amount 30000
    When the system processes the approval request
    Then the task is assigned to an approver with role "Tier2ApproverRole"
    And no other approvers outside the threshold have the task

  @tier3
  Scenario: Approval request with amount within Tier 3 threshold
    Given a user submits an approval request with amount 60000
    When the system processes the approval request
    Then the task is assigned to an approver with role "Tier3ApproverRole"
    And no other approvers outside the threshold have the task

