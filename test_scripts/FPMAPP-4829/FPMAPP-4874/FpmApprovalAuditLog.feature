# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-4874
# Epic: FPMAPP-4829
# Generated on: 2025-08-19 06:03:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit log creation on approval action

  Scenario: Verify audit log entry after approval
    Given I am logged in as a Director
    When I approve a high-value request
    And I access the audit log
    Then I should see an entry in the audit log with my email and approval action