# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4868
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:13:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit log entry for currency rate change

  Scenario: Finance administrator modifies a currency rate
    Given the finance administrator is logged into the admin UI
    When the finance administrator navigates to the currency overrides section
    And modifies the currency rate for "USD" from "1.00" to "1.25"
    And saves the changes
    Then an entry should be created in the audit logs
    And the entry should include the currency code "USD"
    And the entry should include the old rate "1.00"
    And the entry should include the new rate "1.25"
    And the entry should include a timestamp
