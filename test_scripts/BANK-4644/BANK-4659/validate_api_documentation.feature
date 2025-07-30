# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4659
# Epic: BANK-4644
# Generated on: 2025-07-30 17:01:57
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Validate API Documentation

  Scenario: Check API documentation for new approval statuses
    Given the API documentation has been updated
    When I access the API documentation for "/getfpmlist/empid/{empId}"
    Then the documentation should include the new approval status
    And it should provide examples of the response