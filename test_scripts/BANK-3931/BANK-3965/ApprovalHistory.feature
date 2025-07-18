# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3965
# Epic: BANK-3931
# Generated on: 2025-07-18 13:36:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval History
  As a financial analyst
  I want to see the approval progress of financial entries
  So that I can ensure proper governance and traceability

  Scenario: Display previous approvers and comments
    Given historical approval data exists
    And user has sufficient privileges
    When I navigate to the financial entry history section
    Then I should see the previous approvers and comments displayed
    And the approval history section should not be empty