# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8956
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:35:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter for INR to JPY conversions on Transaction History
  
  As a financial user with role-based approval permissions
  I want to filter transaction history by currency pair INR to JPY
  So that I can view only relevant currency conversions accurately

  Background:
    Given the system has multiple currency conversions including INR to USD and INR to JPY
    And role-based approval and real-time currency integration features are enabled

  Scenario: Filter transaction history to show only INR to JPY conversions
    When I navigate to the transaction history page
    And I select the currency filter "INR to JPY"
    And I apply the filter
    Then I should see only transactions with currency pair "INR-JPY"
    And no other currency pairs should be displayed
    And the filtering should respect my role-based approval restrictions
    And the real-time currency integration should not affect the filter accuracy
    And the page should load without performance degradation or UI issues
