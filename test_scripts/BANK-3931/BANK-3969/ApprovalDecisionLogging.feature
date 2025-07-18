# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3969
# Epic: BANK-3931
# Generated on: 2025-07-18 13:32:01
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Decision Logging

  Scenario: View approval history
    Given the Program Director is logged in
    And there is an approved financial entry
    When the Program Director views the approval history for the financial entry
    Then the system should display timestamps for approval decisions

  Background:
    Given the Program Director opens the web application
    And enters valid credentials "program_director" with password "securepassword"
    And the Program Director is on the approval history page
