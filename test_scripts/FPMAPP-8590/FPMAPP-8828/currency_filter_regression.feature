# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8828
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:57:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter Regression Test
  As a user with role-based approval permissions
  I want to filter transaction history by currency pair
  So that I can view only relevant currency conversions with correct audit trail and approval status

  Background:
    Given multiple currency conversions exist in history including "INR to USD" and "INR to JPY"
    And role-based approval and audit trail features are enabled

  Scenario: Filter transaction history by "INR to JPY" currency pair
    When I navigate to the transaction history page
    And I select the currency filter "INR to JPY"
    And I apply the filter
    Then only transactions with currency pair "INR to JPY" should be displayed
    And transactions with other currency pairs should be excluded
    And the approval status for each displayed transaction should be "Approved"
    And the audit trail should be visible and consistent for each displayed transaction
    And the filter should respect role-based approval restrictions
    And the filter behavior should be backward compatible with previous versions
