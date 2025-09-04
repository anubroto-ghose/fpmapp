# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5267
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:15:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Request Historical Currency Data with Invalid Parameters
  As a user of the currency exchange app
  I want to receive a clear error message when entering invalid currency codes or invalid date ranges
  So that I do not get incorrect or misleading historical data

  Background:
    Given the user is logged in
    And the application is connected to the currency exchange API

  Scenario: Requesting historical currency data with invalid currency code and start date after end date
    When the user navigates to the historical currency data request section
    And the user enters an invalid currency code "XX1"
    And the user enters a start date "2024-06-15" that is after the end date "2024-06-10"
    And the user submits the historical data request
    Then an error message indicating invalid parameters should be displayed
    And no historical data should be shown
