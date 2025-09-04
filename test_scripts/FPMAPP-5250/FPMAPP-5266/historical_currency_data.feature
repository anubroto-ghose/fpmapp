# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5266
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:15:56
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Data Retrieval
  As a user,
  I want to access historical currency exchange rates
  So that I can view the currency rate trends between chosen dates

  Background:
    Given the user is logged in as "testuser" with password "securepassword"
    And the application is connected to the currency exchange API

  Scenario: Retrieve historical currency rates for a valid date range
    Given the user navigates to the historical currency data request page
    When the user enters a valid currency code "USD"
    And the user selects a start date "2024-05-01" and an end date "2024-05-03"
    And the user submits the historical data request
    Then the system fetches and displays the historical currency rates accurately for the specified date range
    And the displayed data contains rates for the dates "2024-05-01", "2024-05-02", and "2024-05-03" with correct rates
