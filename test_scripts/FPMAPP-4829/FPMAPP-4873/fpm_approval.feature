# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4873
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:08:37
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manager Approval for Mid-Tier Requests

  Scenario: Approve a mid-tier request as a Manager
    Given I am logged in as a Manager
    When I attempt to approve a mid-tier request
    Then the request should be approved successfully
    And the status should reflect the approval
