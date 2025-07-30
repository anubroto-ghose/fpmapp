# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4662
# Epic: BANK-4644
# Generated on: 2025-07-30 17:01:02
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Currency Override

  Scenario: Admin overrides currency rate
    Given the admin is logged into the admin interface
    When the admin performs a manual override of the currency rate for "USD" to "1.25"
    Then the system logs should contain an entry for the currency override with code "USD", new rate "1.25", and the timestamp of the override
