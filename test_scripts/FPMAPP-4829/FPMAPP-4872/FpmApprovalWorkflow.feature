# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4872
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:09:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow

  Scenario: Director approves a high-value request
    Given I am logged in as a Director
    When I attempt to approve a high-value request
    Then the request should be approved successfully
    And the status should reflect the approval
