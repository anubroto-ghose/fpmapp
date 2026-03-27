# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8633
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:51:52
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Workflow Role Threshold Enforcement
  As a financial approver
  I want hierarchical role-based approval workflows with thresholds
  So that approval requests are routed to the correct approver based on amount

  Background:
    Given the approval workflow system is deployed with role threshold logic enabled
    And roles are defined as:
      | Role          | Threshold Amount |
      | LOWER_LEVEL   | 10000            |
      | MANAGER       | 50000            |
      | DIRECTOR      | No Limit         |

  Scenario Outline: Submit approval requests and verify routing
    When I submit an approval request with amount <amount>
    Then the request should be routed to the <expectedRole>

    Examples:
      | amount  | expectedRole        |
      | 5000    | LOWER_LEVEL_APPROVER |
      | 30000   | MANAGER             |
      | 100000  | DIRECTOR            |

  Scenario: No request bypasses the defined role thresholds
    Given I submit approval requests with amounts:
      | amount |
      | 5000   |
      | 15000  |
      | 60000  |
    Then each request should be routed according to the role thresholds:
      | amount | expectedRole        |
      | 5000   | LOWER_LEVEL_APPROVER |
      | 15000  | MANAGER             |
      | 60000  | DIRECTOR            |
