# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5265
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:16:34
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Exchange Rates Access
  
  As a logged-in user
  I want to access real-time currency exchange rates
  So that I can view accurate and latest currency conversion data

  Background:
    Given the user is logged in with username "testuser" and password "TestPassword123!"
    And the currency exchange API is mocked to return current rates

  Scenario: Successful retrieval and display of real-time currency exchange rates
    When the user navigates to the currency exchange rates page
    Then the system fetches the current currency rates
    And the user observes the displayed currency rates as:
      | Currency | Rate  |
      | USD      | 1.0   |
      | EUR      | 0.91  |
      | GBP      | 0.79  |
      | JPY      | 134.2 |
