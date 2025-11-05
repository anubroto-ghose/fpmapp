# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7210
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:13:13
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Get FPM List

  Scenario: Retrieve FPM records with valid JWT token
    Given I have a valid JWT token
    When I send a GET request to "/api/fpm/list" with the token
    Then I should receive a response with status 200
      And the response should contain the list of FPM records

  Scenario: Attempt to retrieve FPM records with invalid JWT token
    Given I have an invalid JWT token
    When I send a GET request to "/api/fpm/list" with the token
    Then I should receive a response with status 401