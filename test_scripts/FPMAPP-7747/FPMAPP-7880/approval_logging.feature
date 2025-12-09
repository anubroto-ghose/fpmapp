# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7880
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:19:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Logging

  Scenario: Log approval actions with user details
    Given the user is logged in as a manager
    When the user approves a request
    Then the approval action should be logged with the correct user details and timestamp
    And the log entry should correspond to the approved request