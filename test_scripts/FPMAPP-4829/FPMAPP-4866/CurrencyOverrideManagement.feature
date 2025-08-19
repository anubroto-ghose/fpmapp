# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4866
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:12:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manage Currency Overrides

  As a finance administrator,
  I want to manage currency overrides
  So that I can ensure accurate currency rates are applied.

  Scenario: Successfully save a currency override
    Given I am logged in as a finance administrator
    When I navigate to the currency overrides section
    And I enter valid currency code "USD" and rate "1.25"
    And I click on the "Save" button
    Then the currency override is successfully saved
    And a confirmation message is displayed "Currency override saved successfully!"