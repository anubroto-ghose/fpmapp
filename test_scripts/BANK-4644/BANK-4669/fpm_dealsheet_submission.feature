# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4669
# Epic: BANK-4644
# Generated on: 2025-07-30 16:59:00
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Dealsheet Submission
  As a financial analyst
  I want to submit dealsheets for approval
  So that I can track financial entries

  Scenario: Submission of dealsheets with pending approval status
    Given the user is logged in as a financial analyst
    When the user navigates to the dealsheet submission page
    And fills in the required fields with valid data
      | field        | value                      |
      | dealsheetTitle | Q1 Financial Overview      |
      | amount       | 10000                     |
      | description  | Overview of Q1 financials |
    And submits the dealsheet for approval
    Then the dealsheet should be submitted with a pending approval status
    And a confirmation message should be displayed to the user