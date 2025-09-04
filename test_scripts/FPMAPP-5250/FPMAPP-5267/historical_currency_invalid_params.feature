# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5267
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:41:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Historical Currency Data Retrieval Validation
  As a logged-in user connected to the currency exchange API,
  I want to receive errors when requesting historical currency data with invalid parameters

  Background:
    Given the user is logged in
    And the user is on the Historical Currency Data page

  @FPMAPP-5252-TC03
  Scenario: User enters invalid currency code and start date after end date
    When the user enters an invalid currency code "INVALID"
    And the user enters the start date "2025-12-31" and the end date "2025-01-01"
    And submits the historical currency data request
    Then the system should display an error message indicating invalid parameters
    And no historical currency data should be displayed
