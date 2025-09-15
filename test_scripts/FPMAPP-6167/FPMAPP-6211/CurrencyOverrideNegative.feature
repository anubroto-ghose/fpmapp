# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6211
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:47:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Rate Override Negative Tests
  As a finance administrator
  I want to be prevented from overriding currency rates with invalid currency codes
  So that integrity of currency data and audit logs are maintained

  Background:
    Given the currency override API endpoint is deployed and accessible
    And the admin user is authorized to perform currency rate overrides

  Scenario: Attempt override with invalid currency code
    When the admin user submits a currency rate override request with an invalid currency code "XZZ"
    Then the API should return a 400 Bad Request response
    And the response should contain a descriptive error message indicating invalid currency code
    And no currency rate override changes should be applied in the database
    And no audit log should be created for the invalid override attempt
    And no alert notification emails should be sent
