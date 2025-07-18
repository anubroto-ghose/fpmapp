# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3962
# Epic: BANK-3931
# Generated on: 2025-07-18 13:39:28
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Manual Rate Override Feature

  Scenario: Override a specific currency rate manually
    Given live exchange rates are available
    When I access the manual rate override feature
    And I input the desired currency pair "USD/EUR" and rate "0.85"
    Then the system should accept the manual rate override
    And the system should use the specified rate in currency conversions
