# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8934
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:55:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: High-Value Financial Request Approval Routing
  As a financial approver
  I want high-value financial requests to be routed only to directors
  So that hierarchical role-based approval workflows are enforced

  Background:
    Given the system has role mappings configured with a high-value threshold of 100000.00
    And a user with the role "director" exists and is active
    And a financial request with a value above the high-value threshold is created

  Scenario: Submit and approve a high-value financial request
    When a financial user submits a financial approval request with amount 150000.00
    Then the request should be routed only to users with the role "director"
    When the director user "directorUser" approves the request
    Then the approval status of the request should be "APPROVED"
    And no user without the "director" role can approve the request
