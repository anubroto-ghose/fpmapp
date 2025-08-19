# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4868
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:10:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Override Audit Log

  Scenario: Audit log entry for currency rate change
    Given the finance administrator is logged into the admin UI
    When I navigate to the currency overrides section
    And I modify the currency rate for "USD" from "1.00" to "1.10"
    And I save the changes
    Then an entry should be created in the audit logs
    And the entry should include the currency code "USD"
    And the entry should include the old rate "1.00"
    And the entry should include the new rate "1.10"
    And the entry should include a timestamp