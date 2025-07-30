# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4660
# Epic: BANK-4644
# Generated on: 2025-07-30 17:01:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Override of Currency Rates

  Scenario: Successful manual override of currency rates
    Given the admin is logged into the admin interface
    When the admin navigates to the currency override section
    And enters a valid currency code "USD" and a new rate "1.25"
    And submits the override
    Then the currency rate is updated successfully
    And a confirmation message "Currency rate updated successfully!" is displayed
