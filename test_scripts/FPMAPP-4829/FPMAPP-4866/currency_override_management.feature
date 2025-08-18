# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4866
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:15:54
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manage Currency Overrides

  As a finance administrator,
  I want to manage currency overrides
  So that I can ensure accurate currency rates are applied.

  Scenario: Successful management of currency overrides
    Given the finance administrator is logged into the admin UI
    When I navigate to the currency overrides section
    And I enter "USD" as the currency code
    And I enter "1.25" as the currency rate
    And I click on the "Save" button
    Then the currency override is successfully saved
    And a confirmation message is displayed