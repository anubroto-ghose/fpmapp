# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5270
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:15:18
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Auditor views approval history

  As a financial auditor with auditor role
  I want to view the complete approval history
  So that I can verify approvals with timestamps and user details

  Background:
    Given the auditor "auditor01" is logged into the system

  Scenario: Viewing approval history for deal sheets within a date range
    When the auditor navigates to the audit history section
    And selects a start date of "2025-08-20"
    And selects an end date of "2025-09-04"
    And submits the audit history search
    Then the auditor should see approval history entries
      | dealSheetId   | approver  | status   | auditedBy | actionTimestamp        |
      | DealSheet-001 | ManagerA  | APPROVED | auditor01 | 2025-08-25 10:15:00   |
      | DealSheet-002 | DirectorB | REJECTED | auditor01 | 2025-08-26 11:45:00   |
      | DealSheet-003 | ManagerC  | APPROVED | auditor01 | 2025-09-01 09:30:00   |
