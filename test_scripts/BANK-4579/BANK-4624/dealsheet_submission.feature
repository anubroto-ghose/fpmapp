# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4624
# Epic: BANK-4579
# Generated on: 2025-07-30 04:56:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Dealsheet Submission

  Scenario: Submit a dealsheet and check status
    Given the user is logged in as a financial manager
    When the user submits a dealsheet with valid data
    Then the dealsheet status should be 'Pending Approval'
