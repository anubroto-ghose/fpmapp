# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6178
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:13:24
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Fetch Current Currency Exchange Rates
  As a finance administrator
  I want to fetch the current currency exchange rates via API
  So that I can view and validate the latest exchange rate data

  Background:
    Given the currency exchange rate service is deployed and accessible
    And the database contains valid current exchange rate data
    And the user has valid authorization to fetch currency rates

  Scenario: Successfully fetch current currency exchange rates
    When the user calls the API endpoint to fetch current currency exchange rates
    Then the HTTP response status code should be 200
    And the response payload should contain all required currency pairs with current exchange rates
    And the response payload timestamp should correspond to the latest synchronized data
    And the response should contain no errors or missing data