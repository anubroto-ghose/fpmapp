# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3967
# Epic: BANK-3931
# Generated on: 2025-07-18 13:34:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Program Director Approval of Financial Entries
  As a Program Director,
  I want to approve financial entries
  So that I can ensure proper governance and control of financial data.

  Scenario: Approving financial entries
    Given Program Director is logged into the system
    And there are financial entries pending approval
    When Program Director navigates to the approval section
    And selects a financial entry to approve
    And approves the financial entry
    Then the system displays a success message
    And the financial entry status changes to 'Approved'
    And notifications are sent to the initiator