# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4864
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:17:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail
  As a requestor
  I want to track the status of my submissions at every stage of the approval process
  So that I can see the history of my requests

  Scenario: Verify audit trail includes timestamps and user details
    Given the user is logged in
    And the user has access to the request history
    When the user navigates to the request history page
    And selects a specific request to view details
    Then the audit trail section should display timestamps
    And the audit trail section should display user details
