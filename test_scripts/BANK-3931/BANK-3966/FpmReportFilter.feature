# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3966
# Epic: BANK-3931
# Generated on: 2025-07-18 13:35:40
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Report Filtering
  As a financial analyst
  I want to filter financial entries by approval status
  So that I can see the approval progress of financial entries

  Scenario: Filter reports by approval status
    Given the report generation functionality is available
    And financial entry statuses vary
    When I access the report filtering section
    And I apply the filter for approval status
    Then the reports should show entries filtered by status accurately
    Examples:
      | status   | expectedOutcome             |
      | Pending  | should show 'Pending'       |
      | Approved | should NOT show 'Approved'  |
      | Rejected | should NOT show 'Rejected'  
