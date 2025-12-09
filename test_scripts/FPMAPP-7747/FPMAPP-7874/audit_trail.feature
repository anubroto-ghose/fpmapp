# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7874
# Epic: FPMAPP-7747
# Generated on: 2025-12-09 16:24:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Logging

  Scenario: Verify logging of changes after submission
    Given the user is logged in as an auditor
    When the user submits a request
    And the user makes changes to the request after submission
    And the user accesses the audit trail logs
    Then the audit trail should show the changes made after submission with the correct user details and timestamps
