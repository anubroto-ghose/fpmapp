# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-34
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:45:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging

  Scenario: Successful logging of rejection with user details
    Given the system is set up to log audit trails for rejections
    When I log in as an authorized user
      | username       | password    |
      | authorizedUser | password123 |
    And I reject a request
    Then the audit log should contain an entry with the timestamp and user details for the rejection
    And the entry should include the user "authorizedUser"
    And the entry should indicate the action "Rejection"