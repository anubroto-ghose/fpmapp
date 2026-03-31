# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8936
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:53:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Mid-tier financial approval workflow
  As a financial approver
  I want hierarchical role-based approval workflows
  So that mid-tier requests are routed to managers and approval status is persisted

  Background:
    Given role mappings and thresholds are configured with mid-tier requests routed to managers
    And a user with the role "manager" exists and is active
    And a financial request with a value within the mid-tier threshold is created

  Scenario: Manager approves a mid-tier financial request
    When a user submits a mid-tier financial approval request with amount 5000.00
    Then the request is routed only to managers
    When the manager approves the request
    Then the approval status is updated to "Approved" and visible in the system
    And no bypass of the role hierarchy occurs
