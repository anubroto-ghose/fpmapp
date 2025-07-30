# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4661
# Epic: BANK-4644
# Generated on: 2025-07-30 17:01:23
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Currency Override

  Scenario: Admin attempts to override currency rate with invalid currency code
    Given the admin is logged into the admin interface
    When the admin navigates to the currency override section
    And enters an invalid currency code "INVALID_CODE" and a new rate "1.5"
    And submits the override
    Then an error message is displayed indicating that the currency code is invalid
    And the currency rate remains unchanged