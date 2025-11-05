# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7212
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:12:29
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Access Control for FPM List

  As a system administrator
  I want to ensure that JWT validation is enforced
  So that unauthorized users cannot access specific FPM records

  Scenario: Accessing FPM List with Unauthorized Employee ID
    Given I have a valid JWT token for employee ID "12345"
    When I send a GET request to "/api/fpm/list"
    Then I should receive a response status of "403 Forbidden"
    And the response should include the message "Access Denied: You do not have permission to access these FPM records."
