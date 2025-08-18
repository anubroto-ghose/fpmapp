# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4874
# Epic: FPMAPP-4829
# Generated on: 2025-08-18 14:08:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit log creation on approval action

  Scenario: Approve a high-value request and verify audit log entry
    Given I am logged in as a Director
    When I approve a high-value request
    Then I should see an entry in the audit log with my approval action details
