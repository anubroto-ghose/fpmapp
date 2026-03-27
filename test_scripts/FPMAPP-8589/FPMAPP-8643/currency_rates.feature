# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8643
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:44:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rates API
  As a financial analyst
  I want to fetch current and historical currency exchange rates with admin override information
  So that I can make informed financial decisions based on accurate and up-to-date data

  Background:
    Given the API endpoint "/fpm/currency/rates" is deployed and accessible
    And the Currency_Exchange_Rates table contains current and historical data with some admin overrides

  Scenario: Fetch current currency rates without timestamp
    When I call the currency rates API without a timestamp parameter
    Then the response should include current rates
    And each rate should include override flags and metadata if applicable

  Scenario: Fetch historical currency rates with a valid timestamp
    Given a valid historical timestamp "2024-05-01T00:00:00Z"
    When I call the currency rates API with the timestamp parameter
    Then the response should include rates as of the given timestamp
    And each rate should include override flags and metadata if applicable

  Scenario: Fetch currency rates with an invalid or future timestamp
    Given an invalid or future timestamp "2999-01-01T00:00:00Z"
    When I call the currency rates API with the timestamp parameter
    Then the API should return an appropriate error message or an empty response
