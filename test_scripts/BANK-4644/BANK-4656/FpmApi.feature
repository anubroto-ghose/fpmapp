# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4656
# Epic: BANK-4644
# Generated on: 2025-07-30 17:02:50
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM API Tests

  Scenario: Verify GET /getfpmlist/empid/{empId} returns 400 for missing empId
    Given the API is updated to return the new approval status
    When I send a GET request to "/getfpmlist/empid/" without providing an empId
    Then the response should return a 400 status code
    And the response should contain "empId is required"