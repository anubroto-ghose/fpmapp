# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8814
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:47:29
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Submission Validation
  As an administrator
  I want to ensure that currency override submissions require a reason
  So that incomplete override requests are rejected and no audit or alert is generated

  Background:
    Given the user is logged in with administrator role
    And the currency override API endpoint is accessible

  Scenario: Override submission fails with missing reason field
    When the user submits a currency override request with a valid new exchange rate but without a reason
    Then the API response should be a validation error indicating the missing reason
    And no new override log entry should be created
    And no alert should be generated
    And the currency data should remain unchanged
