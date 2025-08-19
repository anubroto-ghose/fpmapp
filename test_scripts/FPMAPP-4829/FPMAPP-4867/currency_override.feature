# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4867
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:11:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Management

  Scenario: Handling of invalid currency code
    Given the finance administrator is logged into the admin UI
    When the finance administrator navigates to the currency overrides section
    And enters an invalid currency code "INVALIDCODE" and a valid rate "1.23"
    And clicks on the "Save" button
    Then an error message is displayed indicating that the currency code is invalid
    And the currency override is not saved