# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4622
# Epic: BANK-4579
# Generated on: 2025-07-30 04:57:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Dealsheet Submission

  Scenario: Successful dealsheet submission triggers approval workflow
    Given the user is logged in as a financial manager
    When the user navigates to the dealsheet submission page
    And fills out the dealsheet form with valid data
    And clicks the 'Submit' button
    Then the system triggers the approval workflow
    And a confirmation message is displayed to the user