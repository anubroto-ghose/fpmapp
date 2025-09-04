# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5270
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:13:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Auditor's Access to Approval History
  As an auditor
  I want to view the approval history for deal sheets
  So that I can review all approvals and rejections with full details

  Background:
    Given the auditor is logged in with auditor role

  Scenario: View approval history within a given timeframe
    When the auditor navigates to the audit history section
    And the auditor selects a time frame from "2024-06-01" to "2024-06-15"
    Then the approval history for all deal sheets processed in that period is displayed
    And each record shows deal sheet id, approval status, auditor user, and timestamp

  Scenario Outline: Verify individual approval history records
    Given the following approval history records exist:
      | DealSheetId   | Status   | User       | Timestamp           |
      | <DealSheetId> | <Status> | <User>     | <Timestamp>         |
    When the auditor views the approval history from "2024-06-01" to "2024-06-15"
    Then the approval history contains a record with deal sheet id "<DealSheetId>", status "<Status>", user "<User>", and timestamp "<Timestamp>"

    Examples:
      | DealSheetId    | Status   | User       | Timestamp           |
      | DealSheet#1234 | approved | john.doe   | 2024-06-10 14:30    |
      | DealSheet#1235 | rejected | jane.smith | 2024-06-11 09:45    |
