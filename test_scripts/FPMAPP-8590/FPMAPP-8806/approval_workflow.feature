# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8806
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:41:14
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Workflow with Hierarchical Role Mapping and Financial Thresholds
  As a director or manager
  I want to approve deal sheets, staffing, and travel requests based on financial thresholds
  So that approvals are routed correctly and enforced by role

  Background:
    Given the system has users with roles "DIRECTOR" and "MANAGER"
    And the user "directorUser" is logged in as a "DIRECTOR"
    And the user "managerUser" is logged in as a "MANAGER"

  Scenario: Director approves a deal sheet within financial threshold
    Given a deal sheet request is created with amount 50000
    When the request is submitted
    Then the request is routed to the "DIRECTOR" approver
    When the "directorUser" approves the request
    Then the request status updates to "APPROVED" in real-time
    And the requester can see the updated approval status as "APPROVED"

  Scenario: Manager cannot approve a deal sheet above their financial threshold
    Given a deal sheet request is created with amount 150000
    When the request is submitted
    Then the request is routed to the "DIRECTOR" approver
    When the "managerUser" attempts to approve the request
    Then the approval action is denied
    And the request status remains "PENDING"

  Scenario: Manager approves a travel request within financial threshold
    Given a travel request is created with amount 20000
    When the request is submitted
    Then the request is routed to the "MANAGER" approver
    When the "managerUser" approves the request
    Then the request status updates to "APPROVED" in real-time
    And the requester can see the updated approval status as "APPROVED"
