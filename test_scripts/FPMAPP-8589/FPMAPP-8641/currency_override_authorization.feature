# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8641
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:45:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Exchange Rate Override Authorization
  As a non-admin user
  I want to be prevented from overriding currency exchange rates
  So that unauthorized changes are not made and audit integrity is maintained

  Background:
    Given the user "nonadminuser" with password "password123" is authenticated
    And the Currency_Exchange_Rates table is accessible

  Scenario: Non-admin user attempts to override currency exchange rates
    When the user sends a PUT request to "/fpm/currency/rates" with body:
      | overrideFlag  | true             |
      | overrideReason| Unauthorized test |
    Then the API response status should be 403 Forbidden
    And the response should contain an authorization error message
    And no changes should be made to the Currency_Exchange_Rates table
    And no alert emails should be sent
    And no audit log entry should be created
