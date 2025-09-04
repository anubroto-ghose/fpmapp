# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5417
# Epic: FPMAPP-5363
# Generated on: 2025-09-04 16:07:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Routing Based on Financial Threshold
  As an approver
  I want approval requests to be routed to the correct approver based on the financial amount threshold
  So that hierarchical role-based approval routing is enforced

  Background:
    Given the approval workflows and roles with financial thresholds are configured in the system

  Scenario: Approval request routed to mid-tier approver for amount within mid-tier threshold
    Given a user "requester_user" with role "REQUESTER" is logged into the FPM application
    When the user submits an approval request with an amount of 150000.00
    Then the approval request is assigned to an approver with role "MID_TIER_APPROVER"
    And only the approver with role "MID_TIER_APPROVER" receives the approval task
    And the workflow task assignment details reflect the correct approver with username "approver_mid_tier"

  Scenario Outline: Approval request routed to correct approver for different amounts
    Given a user "requester_user" with role "REQUESTER" is logged into the FPM application
    When the user submits an approval request with an amount of <amount>
    Then the approval request is assigned to an approver with role "<expectedRole>"
    And only the approver with role "<expectedRole>" receives the approval task
    And the workflow task assignment details reflect the correct approver with username "<expectedApprover>"

    Examples:
      | amount   | expectedRole        | expectedApprover      |
      | 5000.00  | LOW_TIER_APPROVER   | approver_low_tier     |
      | 150000.00| MID_TIER_APPROVER   | approver_mid_tier     |
      | 500000.00| HIGH_TIER_APPROVER  | approver_high_tier    |
