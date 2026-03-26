# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8821
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:52:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Hierarchical Approval Workflow
  As an approver
  I want approval requests to be routed based on user roles and financial thresholds
  So that requests are escalated properly according to amount

  Background:
    Given the system has user accounts with roles and defined financial thresholds
    And the approval workflow is configured with role-based routing and financial thresholds

  Scenario: Submit approval request below first role's financial threshold
    When I submit an approval request with amount 5000
    Then the request should be routed to the "Manager" role

  Scenario: Submit approval request exceeding first role's threshold but within next role's threshold
    When I submit an approval request with amount 30000
    Then the request should be routed to the "Director" role

  Scenario: Submit approval request exceeding all defined financial thresholds
    When I submit an approval request with amount 100000
    Then the request should be routed to the "VP" role
