# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7224
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:08:10
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Response time for paginated data retrieval

  Scenario: Check response time for loading large dataset
    Given I am on the FPM records page
    When I trigger the action to load records up to 10,000
    Then the response time must not exceed 600 milliseconds
