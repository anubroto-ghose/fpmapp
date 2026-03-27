# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8648
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:41:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Routing Based on Role Thresholds
  
  As an approver
  I want hierarchical role-based approval workflows with thresholds
  So that approval requests are routed to the correct role-based users

  Background:
    Given approval workflows are configured with role thresholds for director, manager, and others

  Scenario Outline: Submit approval request and verify routing
    When I submit an approval request with roleThreshold set to "<roleThreshold>"
    Then the request should be routed to a user with the "<expectedRole>" role
    And the system logs the routing decision with roleThreshold details

    Examples:
      | roleThreshold | expectedRole |
      | director      | director     |
      | manager       | manager      |
      | employee      | others       |

  Scenario: Requests with undefined roleThreshold values are routed to the default role group
    When I submit an approval request with roleThreshold set to "intern"
    Then the request should be routed to a user with the "others" role
    And the system logs the routing decision with roleThreshold details

  Scenario: No requests are routed to unauthorized roles
    When I submit an approval request with roleThreshold set to "director"
    Then the request should not be routed to users without the "director" role

