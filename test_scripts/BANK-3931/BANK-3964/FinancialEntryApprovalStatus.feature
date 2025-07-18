# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3964
# Epic: BANK-3931
# Generated on: 2025-07-18 13:37:21
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Display Approval Status on Financial Entry Page

  Scenario: User views approval status
    Given the financial entry page is accessible
    And the user has appropriate permissions
    When they open the financial entry page
    Then the approval status should be visibly displayed
