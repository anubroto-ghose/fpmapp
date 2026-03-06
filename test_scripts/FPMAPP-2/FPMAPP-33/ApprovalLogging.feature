# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-33
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:45:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Logging

  Scenario: Successful logging of approval with user details
    Given the system is set up to log audit trails for approvals
    When I log in as an authorized user
    And I approve a request
    Then the audit log should contain an entry with the timestamp and user details for the approval
