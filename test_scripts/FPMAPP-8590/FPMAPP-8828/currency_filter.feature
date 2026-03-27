# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8828
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:44:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter on Transaction History
  As a financial user
  I want to filter transaction history by currency pairs
  So that I can view only relevant currency conversion transactions with correct audit and approval info

  Background:
    Given the user is logged in with role "ROLE_MANAGER"
    And multiple currency conversions exist in history including "INR to USD" and "INR to JPY"
    And role-based approval and audit trail are enabled

  Scenario: Filter transaction history by "INR to JPY" currency pair
    When the user navigates to the transaction history page
    And the user selects the currency filter "INR to JPY"
    And the user applies the filter
    Then only transactions with currency pair "INR to JPY" are displayed
    And the approval status is visible and respects the user's role-based restrictions
    And the audit trail data is visible and consistent for each filtered transaction
    And the filter dropdown still contains previous currency filter options

  Scenario: Filter transaction history by "INR to USD" currency pair
    When the user navigates to the transaction history page
    And the user selects the currency filter "INR to USD"
    And the user applies the filter
    Then only transactions with currency pair "INR to USD" are displayed
    And the approval status is visible and respects the user's role-based restrictions
    And the audit trail data is visible and consistent for each filtered transaction
    And the filter dropdown still contains previous currency filter options
