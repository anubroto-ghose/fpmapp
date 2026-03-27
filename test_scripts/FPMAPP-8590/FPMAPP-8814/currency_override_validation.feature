# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8814
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 07:53:03
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Submission Validation
  As an administrator
  I want to ensure that currency override submissions without a reason are rejected
  So that incomplete override attempts do not affect system data or generate alerts

  Background:
    Given the user is logged in with administrator role
    And the currency override API endpoint is accessible

  Scenario: Submit currency override without reason field
    When the administrator submits a currency override request with exchange rate "1.25" but no reason
    Then the override request is rejected with a validation error indicating the missing reason
    And no new override log entry is created
    And no alert is generated
    And the currency data remains unchanged
