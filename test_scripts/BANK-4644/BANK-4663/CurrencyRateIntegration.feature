# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4663
# Epic: BANK-4644
# Generated on: 2025-07-30 17:00:45
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-Time Currency Rate Integration

  Scenario: Successful fetching of real-time currency rates
    Given the system is connected to the Fixer.io API with a valid API key
    When I trigger the API call to fetch real-time currency rates
    Then the system should successfully fetch and display the current currency rates without errors
