# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9051
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:49:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Override Submission
  As a finance admin
  I want to submit currency rate override requests via the UI
  So that the backend processes the override and audit logs and alerts are generated

  Background:
    Given I am logged in as a finance admin with override submission permissions
    And the CurrencyOverrideAdminPanel component is loaded
    And the backend override API is available and responsive

  Scenario: Submit a valid currency override request and verify backend integration
    When I navigate to the override submission section
    And I enter a valid currency code "USD" and override rate "1.15"
    And I submit the override request
    Then I should see a confirmation message "Override request submitted successfully"
    And the backend API should receive the override request with currency code "USD" and rate 1.15
    And an alert related to the override should be triggered
    And an audit log entry should be created with the user and timestamp details
