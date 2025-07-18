# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3963
# Epic: BANK-3931
# Generated on: 2025-07-18 13:38:37
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Conversion API Failure Handling

  Scenario: User attempts currency conversion during API failure
    Given the currency conversion page is open
    When the user enters 100 USD and selects convert to EUR
    And the API fails to respond
    Then the application should display an error message
    And the error message should say "Unable to retrieve exchange rates. Please try again later."