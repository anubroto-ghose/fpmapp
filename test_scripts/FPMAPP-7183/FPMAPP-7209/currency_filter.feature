# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7209
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:13:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Filter on Transaction History

  Scenario: Filter transaction history by currency
    Given I am on the transaction history page
    When I select "INR to JPY" from the currency filter
    And I apply the filter
    Then I should see only "INR to JPY" conversions in the list
    And no other currency pairs should be displayed