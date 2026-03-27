# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8890
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:30:47
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Routing Based on Role Hierarchy
  
  As an approver
  I want the system to route approval requests based on hierarchical roles
  So that high-value requests are automatically assigned to Directors

  Background:
    Given the system has role hierarchy configured with Directors above Managers
    And a requestor is authorized to submit approval requests

  Scenario: Submit a high-value approval request and verify routing to Director
    Given a high-value approval request exceeding the mid-tier threshold
    When the requestor submits the approval request
    Then the request is routed automatically to a Director
    And the Director receives an email notification
    And the Director receives an in-app notification
    And no other roles outside the Director role receive the request
    And the approval status updates are reflected in real-time for the requester

  Scenario: Verify unauthorized roles do not receive high-value approval requests
    Given a high-value approval request exceeding the mid-tier threshold
    When the requestor submits the approval request
    Then the Manager role does not receive the approval request notification

  # Additional scenarios can be added for low-value requests, multiple approvers, etc.
