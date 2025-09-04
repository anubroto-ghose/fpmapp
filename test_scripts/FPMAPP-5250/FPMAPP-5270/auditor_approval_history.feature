# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5270
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:39:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Auditor views approval audit history
  As an auditor
  I want to view the complete approval history with timestamps and user details
  So that I can verify compliance and audit financial approvals

  Background:
    Given an auditor is logged in with auditor role

  Scenario: Auditor navigates to audit history and views deal sheet approvals within timeframe
    When the auditor navigates to the audit history section
    And selects a timeframe from "2025-08-20" to "2025-08-30"
    Then the auditor should see the complete approval history
    And the history entries include timestamps and user details
    And deal sheets processed within the timeframe are displayed with approval or rejection status
