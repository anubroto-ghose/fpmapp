# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7211
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:12:48
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM List Access

  Scenario: Unauthorized access on GetFPMList with invalid JWT
    Given the user has an invalid JWT token
    When the user sends a GET request to "/api/fpm/list"
    Then the system should respond with a "401 Unauthorized" message
